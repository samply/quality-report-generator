package de.samply.reporter.utils;

public class ExternalSheetUtilsException extends Exception {

  public ExternalSheetUtilsException(String message) {    super(message);
  }

  public ExternalSheetUtilsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExternalSheetUtilsException(Throwable cause) {
    super(cause);
  }

  public ExternalSheetUtilsException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
