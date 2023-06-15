package de.samply.reporter.template;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.list;
import static java.nio.file.Files.readString;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.template.script.ScriptParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportTemplateManager {

  private final Map<String, ReportTemplate> idQualityReportTemplateMap = new HashMap<>();

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
    if (exists(templateDirectory)) {
      try (Stream<Path> pathStream = list(templateDirectory)) {
        pathStream.filter(path -> !isDirectory(path)).forEach(this::loadTemplate);
      }
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
    return fetchTemplate(readString(templatePath));
  }

  public ReportTemplate fetchTemplate(String template) throws IOException {
    return new XmlMapper().readValue(ScriptParser.readTemplateAndParseScripts(template),
        ReportTemplate.class);
  }

  public ReportTemplate getQualityReportTemplate(String qualityReportTemplateId) {
    return idQualityReportTemplateMap.get(qualityReportTemplateId);
  }

}
