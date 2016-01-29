package com.scu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Manhong Ren (manhongren@hotmail.com) on 1/24/16.
 */
class ClientTask implements Runnable {
    private static final int MAX_TIME_OUT = 15;
    private static final int MAX_TASK = 100;

    private static final String CONNECTION = "Connection";
    private static final String HTTP_10 = "HTTP/1.0";


    private final Socket clientSocket;

    ClientTask(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            final HttpRequest httpRequest = HttpRequest.parseFromInputStream(clientSocket.getInputStream());
            final File file = FileLoader.getFileFromURL(httpRequest.getUrl());

            if (!file.exists()) {
                System.out.println("Resource Not Found: " + FileLoader.getDocumentRoot() + httpRequest.getUrl().substring(1));
                sendError(HttpStatusCode.HTTP_404, httpRequest);
                return;
            }

            if (!file.canRead() || !canRead(file)) {
                sendError(HttpStatusCode.HTTP_403, httpRequest);
                return;
            }
            sendFile(httpRequest, file);
        } catch (HttpRequest.ParseException e) {
            if (!clientSocket.isClosed()) {
                try {
                    sendError(HttpStatusCode.HTTP_400, null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!clientSocket.isClosed()) {
                try {
                    sendError(HttpStatusCode.HTTP_500, null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void sendError(final HttpStatusCode statusCode, HttpRequest httpRequest) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        final String httpVersion = httpRequest == null ? HTTP_10 : httpRequest.getVersion();
        out.println(httpVersion + " " + statusCode.fullDescription());
        out.println("Date: " + getServerTime());
        out.flush();
        out.close();
        clientSocket.close();
    }

    private void sendFile(final HttpRequest httpRequest, final File file) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

        out.println(httpRequest.getVersion() + " " + HttpStatusCode.HTTP_200.fullDescription());

        final ContentType contentType = ContentType.getContentTypeFromFileName(file.getName());
        if (contentType != null) {
            out.println("Content-Type: " + contentType.getType());
        }

        if (httpRequest.getHeaders().containsKey(CONNECTION)) {
            final String connection = httpRequest.getHeaders().get(CONNECTION);
            out.println(CONNECTION + ": " + connection);
            final long queueTaskCount = Server.getNotCompleteTaskCount();
            int timeoutInSeconds = calculateTimeoutInSecond(queueTaskCount);
            out.println("Keep-Alive: timeout=" + timeoutInSeconds + ", max=" + MAX_TASK);
        }

        out.println("Content-Length: " + file.length());
        out.println("Date: " + getServerTime());
        out.println("Last-Modified: " + getLastModifiedDateFromFile(file));

        out.println();
        out.flush();

        Files.copy(file.toPath(), clientSocket.getOutputStream());
        clientSocket.getOutputStream().flush();
        out.flush();
        out.close();
        clientSocket.close();
    }

    private String getTimeStringFromDate(final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    private String getServerTime() {
        return getTimeStringFromDate(new Date(System.currentTimeMillis()));
    }

    private String getLastModifiedDateFromFile(final File file) {
        return getTimeStringFromDate(new Date(file.lastModified()));
    }

    private int calculateTimeoutInSecond(final long queuedTaskCount) {
        return Math.max(0, (int) (MAX_TIME_OUT * (1.0 - Math.floor(queuedTaskCount * 1.0 / MAX_TASK))));
    }

    private boolean canRead(final File file) {
        try {
            new FileReader(file.getPath());
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
