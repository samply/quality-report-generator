package de.samply.qualityreportgenerator.report;

import de.samply.qualityreportgenerator.exporter.ExporterClient;
import de.samply.qualityreportgenerator.exporter.ExporterClientException;
import org.springframework.stereotype.Component;

@Component
public class QualityReportGenerator {

  private ExporterClient exporterClient;

  public QualityReportGenerator(ExporterClient exporterClient) {
    this.exporterClient = exporterClient;
  }

  public void generate() throws QualityReportGeneratorException {
    fetchExportFiles();
  }

  private void fetchExportFiles() throws QualityReportGeneratorException {
    try {
      exporterClient.fetchExportFiles(filePath -> generate(filePath));
    } catch (ExporterClientException e) {
      throw new QualityReportGeneratorException(e);
    }
  }

  private void generate(String filePath) {
    System.out.println("Hello");
  }

}
