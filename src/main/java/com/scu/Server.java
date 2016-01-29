package com.scu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Manhong Ren (manhongren@hotmail.com) on 1/24/16.
 */
class Server {

    private static final int THREAD_POOL_SIZE = 10;
    private static final ThreadPoolExecutor threadPool;
    private static int PORT = -1;

    static {
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-document_root") && i + 1 < args.length) {
                System.out.println("Setting -document_root=" + args[i + 1]);
                FileLoader.setDocumentRoot(args[i + 1]);
            } else if (args[i].equals("-port") && i + 1 < args.length) {
                System.out.println("Setting -port=" + args[i + 1]);
                PORT = Integer.parseInt(args[i + 1]);
            }
        }

        if (!FileLoader.isDocumentRootSet() || PORT < 0) {
            System.out.println("Invalid arguments, abort!");
            return;
        }

        Server.startServer();
    }

    public static long getNotCompleteTaskCount() {
        return threadPool.getTaskCount() - threadPool.getCompletedTaskCount();
    }

    private static void startServer() {
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT);
                    System.out.println("Waiting for client to connect");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.submit(new ClientTask(clientSocket));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
