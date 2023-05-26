package de.samply.reporter.template;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.template.script.ScriptParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportTemplateManager {

  private Map<String, ReportTemplate> idQualityReportTemplateMap = new HashMap<>();

  public ReportTemplateManager(
      @Value(ReporterConst.REPORT_TEMPLATE_DIRECTORY_SV) String qualityReportTemplateDirectory
  ) {
    loadTemplates(Path.of(qualityReportTemplateDirectory));
  }


  private void loadTemplates(Path templateDirectory) {
    try {
      loadTemplatesWithoutExceptionHandling(templateDirectory);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadTemplatesWithoutExceptionHandling(Path templateDirectory)
      throws IOException {
    if (Files.exists(templateDirectory)) {
      Files.list(templateDirectory).filter(path -> !Files.isDirectory(path))
          .forEach(filePath -> loadTemplate(filePath));
    }
  }

  private void loadTemplate(Path templatePath) {
    try {
      loadTemplateWithoutExceptionHandling(templatePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadTemplateWithoutExceptionHandling(Path templatePath) throws IOException {
    ReportTemplate reportTemplate = fetchTemplate(templatePath);
    idQualityReportTemplateMap.put(reportTemplate.getId(), reportTemplate);
  }

  public ReportTemplate fetchTemplate(Path templatePath) throws IOException {
    return fetchTemplate(Files.readString(templatePath));
  }

  public ReportTemplate fetchTemplate(String template) throws IOException {
    return new XmlMapper().readValue(ScriptParser.readTemplateAndParseScripts(template),
        ReportTemplate.class);
  }

  public ReportTemplate getQualityReportTemplate(String qualityReportTemplateId) {
    return idQualityReportTemplateMap.get(qualityReportTemplateId);
  }

}
