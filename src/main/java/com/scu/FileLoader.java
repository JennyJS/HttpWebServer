package com.scu;

import java.io.File;

/**
 * Created by Manhong Ren (manhongren@hotmail.com) on 1/25/16.
 */
class FileLoader {

    private static String DOCUMENT_ROOT;

    public static String getDocumentRoot() {
        return DOCUMENT_ROOT;
    }

    public static void setDocumentRoot(final String documentRoot) {
        DOCUMENT_ROOT = documentRoot.endsWith("/") ? documentRoot : documentRoot + "/";
    }

    public static boolean isDocumentRootSet() {
        return DOCUMENT_ROOT != null;
    }

    public static File getFileFromURL(final String filePath) {
        if (filePath.equals("/")) {
            return new File(DOCUMENT_ROOT + "/index.html");
        }

        final String pathWithoutSlash = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        return new File(DOCUMENT_ROOT + pathWithoutSlash);
    }
}
