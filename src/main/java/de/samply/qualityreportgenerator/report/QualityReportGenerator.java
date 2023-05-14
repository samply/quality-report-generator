package de.samply.qualityreportgenerator.report;

import de.samply.qualityreportgenerator.zip.ExporterUnzipper;
import de.samply.qualityreportgenerator.exporter.ExporterClient;
import de.samply.qualityreportgenerator.exporter.ExporterClientException;
import de.samply.qualityreportgenerator.zip.ExporterUnzipperException;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class QualityReportGenerator {

  private ExporterClient exporterClient;
  private ExporterUnzipper exporterUnzipper;

  public QualityReportGenerator(ExporterClient exporterClient, ExporterUnzipper exporterUnzipper) {
    this.exporterClient = exporterClient;
    this.exporterUnzipper = exporterUnzipper;
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

  }

  private Path[] extractPaths(String filePath) {
    try {
      return exporterUnzipper.extractFiles(filePath);
    } catch (ExporterUnzipperException e) {
      throw new RuntimeException(e);
    }
  }

}
