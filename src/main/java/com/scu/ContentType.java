package com.scu;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manhong Ren (manhongren@apple.com) on 1/26/16.
 */
public enum ContentType {
    HTML    ("text/html",               ".html"),
    CSS     ("text/css",                ".css"),
    JS      ("application/javascript",  ".js"),
    JPG     ("image/jpeg",              ".jpg"),
    PNG     ("image/png",               ".png"),
    TXT     ("text/plain",              ".txt"),
    GIF     ("image/gif",               ".gif");

    private final String type;
    private final String extension;

    private static final Map<String, ContentType> contentTypeByExtension;

    static {
        contentTypeByExtension = new HashMap<>();
        for (ContentType contentType : values()) {
            contentTypeByExtension.put(contentType.extension, contentType);
        }
    }

    ContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType getContentTypeFromFileName(final String fileName) {
        if (fileName.contains(".")) {
            return getContentTypeFromExtension(fileName.substring(fileName.indexOf(".")));
        }
        return null;
    }


    private static ContentType getContentTypeFromExtension(final String extension) {
        return contentTypeByExtension.get(extension);
    }

    public String getType() {
        return type;
    }
}
