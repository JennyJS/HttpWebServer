package com.scu;

/**
 * Created by Manhong Ren (manhongren@hotmail.com) on 1/26/16.
 */
public enum HttpStatusCode {
    HTTP_200(200, "OK"),
    HTTP_400(400, "BAD REQUEST"),
    HTTP_404(404, "NOT FOUND"),
    HTTP_403(403, "FORBIDDEN"),
    HTTP_500(500, "INTERNAL SERVER ERROR");

    private final int statusCode;
    private final String description;

    HttpStatusCode(final int statusCode, final String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public String fullDescription() {
        return statusCode + " " + description;
    }
}
