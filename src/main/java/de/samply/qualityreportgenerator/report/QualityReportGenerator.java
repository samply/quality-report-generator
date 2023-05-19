package de.samply.qualityreportgenerator.report;

import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.exporter.ExporterClient;
import de.samply.qualityreportgenerator.exporter.ExporterClientException;
import de.samply.qualityreportgenerator.zip.ExporterUnzipper;
import de.samply.qualityreportgenerator.zip.ExporterUnzipperException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QualityReportGenerator {

  private ExporterClient exporterClient;
  private ExporterUnzipper exporterUnzipper;
  private final Integer workbookWindow;
  private final Path qualityReportsDirectory;
  private final String qualityReportFilenameTemplate;
  private final String timestampFormat;


  public QualityReportGenerator(
      ExporterClient exporterClient,
      ExporterUnzipper exporterUnzipper,
      @Value(QrgConst.EXCEL_WORKBOOK_WINDOW_SV) int workbookWindow,
      @Value(QrgConst.QUALITY_REPORTS_DIRECTORY_SV) String qualityReportsDirectory,
      @Value(QrgConst.QUALITY_REPORT_FILENAME_TEMPLATE_SV) String qualityReportFilenameTemplate,
      @Value(QrgConst.TIMESTAMP_FORMAT_SV) String timestampFormat) {
    this.exporterClient = exporterClient;
    this.exporterUnzipper = exporterUnzipper;
    this.workbookWindow = workbookWindow;
    this.qualityReportsDirectory = Path.of(qualityReportsDirectory);
    this.qualityReportFilenameTemplate = qualityReportFilenameTemplate;
    this.timestampFormat = timestampFormat;
  }

  public void generate() throws QualityReportGeneratorException {
    fetchExportFiles();
  }

  private void fetchExportFiles() throws QualityReportGeneratorException {
    try {
      exporterClient.fetchExportFiles(filePath -> generate(filePath));
    } catch (ExporterClientException | RuntimeException e) {
      throw new QualityReportGeneratorException(e);
    }
  }

  private void generate(String filePath) {
    Path[] paths = extractPaths(filePath);
    Workbook workbook = new SXSSFWorkbook(workbookWindow);
    Arrays.stream(paths).forEach(path -> addPathToWorkbook(workbook, path));
    //TODO
    Path result = writeWorkbookAndGetQualityReportPath(workbook);
    //TODO
  }

  private Path[] extractPaths(String filePath) {
    try {
      return exporterUnzipper.extractFiles(filePath);
    } catch (ExporterUnzipperException e) {
      throw new RuntimeException(e);
    }
  }

  private void addPathToWorkbook(Workbook workbook, Path path) {

  }

  private Path writeWorkbookAndGetQualityReportPath(Workbook workbook) {
    Path result = qualityReportsDirectory.resolve(fetchQualityReportFilename());
    writeWorkbook(result, workbook);
    return result;
  }

  private void writeWorkbook(Path path, Workbook workbook) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
      workbook.write(fileOutputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String fetchQualityReportFilename() {
    String result = qualityReportFilenameTemplate;
    if (result.contains(QrgConst.TEMPLATE_TIMESTAMP)) {
      String timestamp = getTimestamp(timestampFormat);
      result = result.replace(QrgConst.TEMPLATE_TIMESTAMP, timestamp);
    }
    return result;
  }

  private String getTimestamp(String format) {
    if (format == null) {
      format = timestampFormat;
    }
    return new SimpleDateFormat(format).format(Timestamp.from(Instant.now()));
  }


}
