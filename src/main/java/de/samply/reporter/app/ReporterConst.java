package de.samply.reporter.app;

import java.nio.charset.Charset;

public class ReporterConst {


    public final static String DEFAULT_TIMESTAMP_FORMAT = "yyyyMMdd-HH_mm";
    public static final String DEFAULT_CSV_DELIMITER = "\t";
    public static final String DEFAULT_END_OF_LINE = System.lineSeparator();
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();


    // REST Paths
    public static final String INFO = "/info";
    public static final String GENERATE = "/generate";
    public static final String REPORT = "/report";
    public static final String REPORT_TEMPLATE = "/report-template";
    public static final String REPORT_STATUS = "/report-status";
    public static final String REPORTS_LIST = "/reports-list";
    public static final String LOGS = "/logs";
    public static final String REPORT_TEMPLATE_IDS = "/report-template-ids";
    public static final String RUNNING_REPORTS = "/running-reports";

    // REST Parameters
    public static final String REPORT_TEMPLATE_ID = "template-id";
    public static final String EXPORT_URL = "export-url";
    public static final String REPORT_ID = "report-id";
    public static final String LOGS_SIZE = "logs-size";
    public static final String LOGS_LAST_LINE_REPORTER = "logs-last-line-reporter";
    public static final String LOGS_LAST_LINE_EXPORTER = "logs-last-line-exporter";
    public static final String REPORTS_LIST_PAGE_SIZE = "page-size";
    public static final String REPORTS_LIST_PAGE = "page";


    // Exporter Variables
    public final static String EXPORTER_REQUEST = "/request";
    public final static String EXPORTER_LOGS = "/logs";
    public static final String EXPORTER_LOGS_SIZE = "logs-size";
    public static final String EXPORTER_LOGS_LAST_LINE = "logs-last-line";
    public final static String EXPORTER_REQUEST_PARAM_QUERY = "query";
    public final static String EXPORTER_REQUEST_PARAM_QUERY_FORMAT = "query-format";
    public final static String EXPORTER_REQUEST_PARAM_TEMPLATE_ID = "template-id";
    public final static String EXPORTER_REQUEST_PARAM_OUTPUT_FORMAT = "output-format";
    public final static String HTTP_HEADER_API_KEY = "x-api-key";
    public final static String IS_INTERNAL_REQUEST = "internal-request";


    // Environment Variables
    public final static String CROSS_ORIGINS = "CROSS_ORIGINS";
    public final static String CORS_MAX_AGE_IN_SECONDS = "CORS_MAX_AGE_IN_SECONDS";
    public final static String EXPORTER_URL = "EXPORTER_URL";
    public final static String EXPORTER_API_KEY = "EXPORTER_API_KEY";
    public final static String EXPORTER_QUERY = "EXPORTER_QUERY";
    public final static String EXPORTER_QUERY_FORMAT = "EXPORTER_QUERY_FORMAT";
    public final static String EXPORTER_TEMPLATE_ID = "EXPORTER_TEMPLATE_ID";
    public final static String EXPORTER_OUTPUT_FORMAT = "EXPORTER_OUTPUT_FORMAT";
    public final static String TEMP_FILES_DIRECTORY = "TEMP_FILES_DIRECTORY";
    public final static String REPORTS_DIRECTORY = "REPORTS_DIRECTORY";
    public final static String MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT = "MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT";
    public final static String TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT = "TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT";
    public final static String EXCEL_WORKBOOK_WINDOW = "EXCEL_WORKBOOK_WINDOW";
    public final static String REPORT_FILENAME_TEMPLATE = "REPORT_FILENAME_TEMPLATE";
    public final static String TIMESTAMP_FORMAT = "TIMESTAMP_FORMAT";
    public final static String REPORT_TEMPLATE_DIRECTORY = "REPORT_TEMPLATE_DIRECTORY";
    public final static String FILE_CHARSET = "FILE_CHARSET";
    public final static String FILE_END_OF_LINE = "FILE_END_OF_LINE";
    public final static String CSV_DELIMITER = "CSV_DELIMITER";
    public final static String REPORTS_META_INFO_FILENAME = "REPORTS_META_INFO_FILENAME";
    public final static String HTTP_RELATIVE_PATH = "HTTP_RELATIVE_PATH";
    public final static String HTTP_SERVLET_REQUEST_SCHEME = "HTTP_SERVLET_REQUEST_SCHEME";
    public final static String WEBCLIENT_BUFFER_SIZE_IN_BYTES = "WEBCLIENT_BUFFER_SIZE_IN_BYTES";
    public final static String WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS = "WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS";
    public final static String WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS = "WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS";
    public final static String WEBCLIENT_TCP_KEEP_IDLE_IN_SECONDS = "WEBCLIENT_TCP_KEEP_IDLE_IN_SECONDS";
    public final static String WEBCLIENT_TCP_KEEP_INTERVAL_IN_SECONDS = "WEBCLIENT_TCP_KEEP_INTERVAL_IN_SECONDS";
    public final static String WEBCLIENT_TCP_KEEP_CONNECTION_NUMBER_OF_TRIES = "WEBCLIENT_TCP_KEEP_CONNECTION_NUMBER_OF_TRIES";
    public final static String IS_EXPORTER_IN_SAME_SERVER = "IS_EXPORTER_IN_SAME_SERVER";
    public final static String MAX_NUMBER_OF_ROWS_IN_EXCEL_SHEET = "MAX_NUMBER_OF_ROWS_IN_EXCEL_SHEET";


    // Spring Values (SV)
    public final static String HEAD_SV = "${";
    public final static String BOTTOM_SV = "}";
    public final static String DEFAULT_NULL_VALUE = ":#{null}";
    public final static String CROSS_ORIGINS_SV =
            "#{'" + HEAD_SV + CROSS_ORIGINS + ":#{null}" + BOTTOM_SV + "'.split(',')}";
    public final static String CORS_MAX_AGE_IN_SECONDS_SV =
            HEAD_SV + CORS_MAX_AGE_IN_SECONDS + ":360" + BOTTOM_SV;
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
    public final static String REPORTS_DIRECTORY_SV =
            HEAD_SV + REPORTS_DIRECTORY + ":./reports" + BOTTOM_SV;
    public final static String MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT_SV =
            HEAD_SV + MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT + ":4320" + BOTTOM_SV;
    public final static String TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT_SV =
            HEAD_SV + TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT + ":20" + BOTTOM_SV;
    public final static String EXCEL_WORKBOOK_WINDOW_SV =
            HEAD_SV + EXCEL_WORKBOOK_WINDOW + ":30000000" + BOTTOM_SV;
    public final static String REPORT_FILENAME_TEMPLATE_SV =
            HEAD_SV + REPORT_FILENAME_TEMPLATE + ":report-{TIMESTAMP}" + BOTTOM_SV;
    public final static String TIMESTAMP_FORMAT_SV =
            HEAD_SV + TIMESTAMP_FORMAT + ":" + DEFAULT_TIMESTAMP_FORMAT + BOTTOM_SV;
    public final static String REPORT_TEMPLATE_DIRECTORY_SV =
            HEAD_SV + REPORT_TEMPLATE_DIRECTORY + ":./templates" + BOTTOM_SV;
    public final static String FILE_CHARSET_SV =
            HEAD_SV + FILE_CHARSET + DEFAULT_NULL_VALUE + BOTTOM_SV;
    public final static String FILE_END_OF_LINE_SV =
            HEAD_SV + FILE_END_OF_LINE + DEFAULT_NULL_VALUE + BOTTOM_SV;
    public final static String CSV_DELIMITER_SV =
            HEAD_SV + CSV_DELIMITER + DEFAULT_NULL_VALUE + BOTTOM_SV;
    public final static String REPORTS_META_INFO_FILENAME_SV =
            HEAD_SV + REPORTS_META_INFO_FILENAME + ":reports-meta-info.csv" + BOTTOM_SV;
    public final static String HTTP_RELATIVE_PATH_SV =
            HEAD_SV + HTTP_RELATIVE_PATH + ":" + BOTTOM_SV;
    public final static String HTTP_SERVLET_REQUEST_SCHEME_SV =
            HEAD_SV + HTTP_SERVLET_REQUEST_SCHEME + ":http" + BOTTOM_SV;
    public final static String WEBCLIENT_BUFFER_SIZE_IN_BYTES_SV =
            HEAD_SV + WEBCLIENT_BUFFER_SIZE_IN_BYTES + ":#{36 * 1024 * 1024}" + BOTTOM_SV;
    public final static String WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS_SV =
            HEAD_SV + WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS + ":180" + BOTTOM_SV;
    public final static String WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS_SV =
            HEAD_SV + WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS + ":180" + BOTTOM_SV;
    public final static String WEBCLIENT_TCP_KEEP_IDLE_IN_SECONDS_SV =
            HEAD_SV + WEBCLIENT_TCP_KEEP_IDLE_IN_SECONDS + ":300" + BOTTOM_SV;
    public final static String WEBCLIENT_TCP_KEEP_INTERVAL_IN_SECONDS_SV =
            HEAD_SV + WEBCLIENT_TCP_KEEP_INTERVAL_IN_SECONDS + ":60" + BOTTOM_SV;
    public final static String WEBCLIENT_TCP_KEEP_CONNECTION_NUMBER_OF_TRIES_SV =
            HEAD_SV + WEBCLIENT_TCP_KEEP_CONNECTION_NUMBER_OF_TRIES + ":10" + BOTTOM_SV;
    public final static String IS_EXPORTER_IN_SAME_SERVER_SV =
            HEAD_SV + IS_EXPORTER_IN_SAME_SERVER + ":true" + BOTTOM_SV;
    public final static String MAX_NUMBER_OF_ROWS_IN_EXCEL_SHEET_SV =
            HEAD_SV + MAX_NUMBER_OF_ROWS_IN_EXCEL_SHEET + ":100000" + BOTTOM_SV;


    // Other variables:
    public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HTTP_HEADER_CONTENT_DISPOSITION_FILENAME = "filename=";
    public static final int RANDOM_FILENAME_SIZE = 10;
    public static final int RANDOM_REPORT_ID_SIZE = 15;
    public static final String REPORT_META_INFO_FILE_SEPARATOR = ",";
    public static final String APP_NAME = "Quality Report Generator";
    public static final String TEMP_DIRECTORY_PREFIX = "TEMP_";
    public final static String TEMPLATE_START = "{";
    public final static String TEMPLATE_END = "}";
    public final static String TEMPLATE_SEPARATOR = ":";
    public final static String TEMPLATE_TIMESTAMP = "TIMESTAMP";
    public final static String DEFAULT_EXPORTER_FILE_EXTENSION = "zip";
    public final static String DEFAULT_SCRIPT_RESULT_FILE_EXTENSION = "csv";
    public final static String CONTEXT_VARIABLE = "context";
    public final static String CELL_CONTEXT_VARIABLE = "cellContext";
    public final static String EMPTY_EXCEL_CELL = "";
    public final static int BUFFERED_LOGGER_SIZE = 1000;
    public final static String EXPORTER = "exporter";
    public final static String REPORTER = "reporter";

}
