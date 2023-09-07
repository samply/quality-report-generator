package de.samply.reporter.zip;

public class ZipperException extends Exception{

    public ZipperException() {
    }

    public ZipperException(String message) {
        super(message);
    }

    public ZipperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZipperException(Throwable cause) {
        super(cause);
    }

}
