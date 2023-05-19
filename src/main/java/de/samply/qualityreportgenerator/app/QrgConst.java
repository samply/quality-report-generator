package de.samply.qualityreportgenerator.app;

public class QrgConst {


  public final static String DEFAULT_TIMESTAMP_FORMAT = "yyyyMMdd-HH_mm";

  // REST Paths
  public static final String INFO = "/info";
  public static final String GENERATE = "/generate";


  // Exporter Variables
  public final static String EXPORTER_REQUEST = "/request";
  public final static String EXPORTER_REQUEST_PARAM_QUERY = "query";
  public final static String EXPORTER_REQUEST_PARAM_QUERY_FORMAT = "query-format";
  public final static String EXPORTER_REQUEST_PARAM_TEMPLATE_ID = "template-id";
  public final static String EXPORTER_REQUEST_PARAM_OUTPUT_FORMAT = "output-format";
  public final static String HTTP_HEADER_API_KEY = "x-api-key";


  // Environment Variables
  //public final static String CROSS_ORIGINS = "CROSS_ORIGINS";
  public final static String EXPORTER_URL = "EXPORTER_URL";
  public final static String EXPORTER_API_KEY = "EXPORTER_API_KEY";
  public final static String EXPORTER_QUERY = "EXPORTER_QUERY";
  public final static String EXPORTER_QUERY_FORMAT = "EXPORTER_QUERY_FORMAT";
  public final static String EXPORTER_TEMPLATE_ID = "EXPORTER_TEMPLATE_ID";
  public final static String EXPORTER_OUTPUT_FORMAT = "EXPORTER_OUTPUT_FORMAT";
  public final static String TEMP_FILES_DIRECTORY = "TEMP_FILES_DIRECTORY";
  public final static String QUALITY_REPORTS_DIRECTORY = "QUALITY_REPORTS_DIRECTORY";
  public final static String MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT = "MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT";
  public final static String TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT = "TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT";
  public final static String EXCEL_WORKBOOK_WINDOW = "EXCEL_WORKBOOK_WINDOW";
  public final static String QUALITY_REPORT_FILENAME_TEMPLATE = "QUALITY_REPORT_FILENAME_TEMPLATE";
  public final static String TIMESTAMP_FORMAT = "TIMESTAMP_FORMAT";
  public final static String QUALITY_REPORT_TEMPLATE_DIRECTORY = "QUALITY_REPORT_TEMPLATE_DIRECTORY";


  // Spring Values (SV)
  public final static String HEAD_SV = "${";
  public final static String BOTTOM_SV = "}";
  //    public final static String CROSS_ORIGINS_SV =
//            "#{'" + HEAD_SV + CROSS_ORIGINS + ":#{null}" + BOTTOM_SV + "'.split(',')}";
  public final static String EXPORTER_URL_SV =
      HEAD_SV + EXPORTER_URL + BOTTOM_SV;
  public final static String EXPORTER_API_KEY_SV =
      HEAD_SV + EXPORTER_API_KEY + BOTTOM_SV;
  public final static String EXPORTER_QUERY_SV =
      HEAD_SV + EXPORTER_QUERY + ":Patient" + BOTTOM_SV;
  public final static String EXPORTER_TEMPLATE_ID_SV =
      HEAD_SV + EXPORTER_TEMPLATE_ID + ":ccp-qb" + BOTTOM_SV;
  public final static String EXPORTER_QUERY_FORMAT_SV =
      HEAD_SV + EXPORTER_QUERY_FORMAT + ":FHIR_QUERY" + BOTTOM_SV;
  public final static String EXPORTER_OUTPUT_FORMAT_SV =
      HEAD_SV + EXPORTER_OUTPUT_FORMAT + ":CSV" + BOTTOM_SV;
  public final static String TEMP_FILES_DIRECTORY_SV =
      HEAD_SV + TEMP_FILES_DIRECTORY + ":./temp-files" + BOTTOM_SV;
  public final static String QUALITY_REPORTS_DIRECTORY_SV =
      HEAD_SV + QUALITY_REPORTS_DIRECTORY + ":./quality-reports" + BOTTOM_SV;
  public final static String MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT_SV =
      HEAD_SV + MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT + ":600" + BOTTOM_SV;
  public final static String TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT_SV =
      HEAD_SV + TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT + ":20" + BOTTOM_SV;
  public final static String EXCEL_WORKBOOK_WINDOW_SV =
      HEAD_SV + EXCEL_WORKBOOK_WINDOW + ":30000000" + BOTTOM_SV;
  public final static String QUALITY_REPORT_FILENAME_TEMPLATE_SV =
      HEAD_SV + QUALITY_REPORT_FILENAME_TEMPLATE + ":quality-report-{TIMESTAMP}" + BOTTOM_SV;
  public final static String TIMESTAMP_FORMAT_SV =
      HEAD_SV + TIMESTAMP_FORMAT + ":" + DEFAULT_TIMESTAMP_FORMAT + BOTTOM_SV;
  public final static String QUALITY_REPORT_TEMPLATE_DIRECTORY_SV =
      HEAD_SV + QUALITY_REPORT_TEMPLATE_DIRECTORY + ":./templates" + BOTTOM_SV;


  // Other variables:
  public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
  public static final String HTTP_HEADER_CONTENT_DISPOSITION_FILENAME = "filename=";
  public static final int RANDOM_FILENAME_SIZE = 10;
  public static final String APP_NAME = "Quality Report Generator";
  public static final String TEMP_DIRECTORY_PREFIX = "TEMP_";
  public final static String TEMPLATE_TIMESTAMP = "{TIMESTAMP}";

}
