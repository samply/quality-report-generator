package de.samply.qualityreportgenerator.exporter;

public class ExporterClientException extends Exception {
    public ExporterClientException(String message) {
        super(message);
    }

    public ExporterClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExporterClientException(Throwable cause) {
        super(cause);
    }

    public ExporterClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
