package com.scu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manhong Ren (manhongren@hotmail.com) on 1/24/16.
 */
public class HttpRequest {

    static class ParseException extends Exception{}

    private enum Method {
        GET,
        POST,
        PUT,
        DELETE;

        private static final Map<String, Method> methodByName = new HashMap<>();

        static {
            for (Method method : values()) {
                methodByName.put(method.name(), method);
            }
        }

        public static Method fromName(final String methodName) throws ParseException {
            if (!methodByName.containsKey(methodName)) {
                throw new ParseException();
            }
            return methodByName.get(methodName);
        }
    }

    private final Method method;
    private final String version;
    private final String url;
    private final Map<String, String> headers;

    private HttpRequest(final Method method, final String version, final String url, final Map<String, String> headers) {
        this.method = method;
        this.version = version;
        this.url = url;
        this.headers = headers;
    }

    public Method getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Factory method to construct instances of HttpRequest
     * */
    public static HttpRequest parseFromInputStream(final InputStream inputStream) throws IOException, ParseException {
        int charRead;
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {
                sb.append((char) inputStream.read());
                charRead = inputStream.read();
                if (charRead == '\r') {
                    sb.append((char) inputStream.read());
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }
        String[] lines = sb.toString().split("\r\n");

        // Parse HTTP method and protocol version
        String[] methodAndProtoV = lines[0].split(" ");
        if (methodAndProtoV.length != 3){
            throw new ParseException();
        }
        final Method method = Method.fromName(methodAndProtoV[0]);
        final String url = methodAndProtoV[1];
        final String version = methodAndProtoV[2];

        // Parse headers
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length - 1; i++) {
            String[] kvPair = lines[i].split(": ");
            headers.put(kvPair[0], kvPair[1]);
        }

        return new HttpRequest(method, version, url, headers);
    }
}
