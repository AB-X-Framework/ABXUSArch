package org.abx.util;

public class ExceptionHandler {
    public static void handleException(Throwable e) {
        e.printStackTrace(System.err);
    }
}
