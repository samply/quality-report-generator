package de.samply.reporter.report;

public class ReportGeneratorException extends Exception {

  public ReportGeneratorException(String message) {
    super(message);
  }

  public ReportGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReportGeneratorException(Throwable cause) {
    super(cause);
  }

  public ReportGeneratorException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
