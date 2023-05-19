package de.samply.qualityreportgenerator.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class QualityReportTemplateManagerTest {

  private final String directory = "../dktk-quality-report-generator/templates";

  @Test
  void getQualityReportTemplate() {
    QualityReportTemplateManager qualityReportTemplateManager = new QualityReportTemplateManager(
        directory);
    System.out.println("Testing...");
  }
}
