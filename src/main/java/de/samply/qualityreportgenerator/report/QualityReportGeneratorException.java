package de.samply.qualityreportgenerator.report;

public class QualityReportGeneratorException extends Exception {

  public QualityReportGeneratorException(String message) {
    super(message);
  }

  public QualityReportGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  public QualityReportGeneratorException(Throwable cause) {
    super(cause);
  }

  public QualityReportGeneratorException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
