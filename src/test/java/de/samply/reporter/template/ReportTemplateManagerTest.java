package de.samply.reporter.template;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class ReportTemplateManagerTest {

  private final String directory = "../dktk-reporter/templates";

  @Test
  void getQualityReportTemplate() {
    ReportTemplateManager reportTemplateManager = new ReportTemplateManager(
        directory);
    System.out.println("Testing...");
  }
}
