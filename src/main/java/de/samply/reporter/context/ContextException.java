package de.samply.reporter.context;

public class ContextException extends Exception {

  public ContextException(String message) {
    super(message);
  }

  public ContextException(String message, Throwable cause) {
    super(message, cause);
  }

  public ContextException(Throwable cause) {
    super(cause);
  }

  public ContextException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
