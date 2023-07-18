package de.samply.reporter.utils;

public class CloneUtilsException extends Exception {

    public CloneUtilsException(String message) {
        super(message);
    }

    public CloneUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloneUtilsException(Throwable cause) {
        super(cause);
    }

    public CloneUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
