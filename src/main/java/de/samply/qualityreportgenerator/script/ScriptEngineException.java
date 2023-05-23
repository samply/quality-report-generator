package de.samply.qualityreportgenerator.script;

public class ScriptEngineException extends Exception{

  public ScriptEngineException(String message) {
    super(message);
  }

  public ScriptEngineException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScriptEngineException(Throwable cause) {
    super(cause);
  }

  public ScriptEngineException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
