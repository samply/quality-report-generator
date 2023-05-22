package de.samply.qualityreportgenerator.template;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.template.script.ScriptParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QualityReportTemplateManager {

  private Map<String, QualityReportTemplate> idQualityReportTemplateMap = new HashMap<>();

  public QualityReportTemplateManager(
      @Value(QrgConst.QUALITY_REPORT_TEMPLATE_DIRECTORY_SV) String qualityReportTemplateDirectory
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
    QualityReportTemplate qualityReportTemplate = fetchTemplate(templatePath);
    idQualityReportTemplateMap.put(qualityReportTemplate.getId(), qualityReportTemplate);
  }

  public QualityReportTemplate fetchTemplate(Path templatePath) throws IOException {
    return fetchTemplate(Files.readString(templatePath));
  }

  public QualityReportTemplate fetchTemplate(String template) throws IOException {
    return new XmlMapper().readValue(ScriptParser.readTemplateAndParseScripts(template),
        QualityReportTemplate.class);
  }

  public QualityReportTemplate getQualityReportTemplate(String qualityReportTemplateId) {
    return idQualityReportTemplateMap.get(qualityReportTemplateId);
  }

}
