package de.samply.reporter.app;

import de.samply.reporter.report.ReportGenerator;
import de.samply.reporter.report.ReportGeneratorException;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.ReportTemplateManager;
import de.samply.reporter.utils.ProjectVersion;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReporterController {

  private final String projectVersion = ProjectVersion.getProjectVersion();
  private ReportGenerator reportGenerator;
  private ReportTemplateManager reportTemplateManager;

  public ReporterController(
      ReportGenerator reportGenerator,
      ReportTemplateManager reportTemplateManager) {
    this.reportGenerator = reportGenerator;
    this.reportTemplateManager = reportTemplateManager;
  }

  //@CrossOrigin(origins = "${CROSS_ORIGINS}", allowedHeaders = {"Authorization"})
  @GetMapping(value = ReporterConst.INFO)
  public ResponseEntity<String> info() {
    return new ResponseEntity<>(projectVersion, HttpStatus.OK);
  }

  @GetMapping(value = ReporterConst.GENERATE)
  public ResponseEntity<String> generate(
      @RequestParam(name = ReporterConst.REPORT_TEMPLATE_ID, required = false) String templateId,
      @RequestHeader(name = "Content-Type", required = false) String contentType,
      @RequestBody(required = false) String template
  ) throws ReportGeneratorException {
    ReportTemplate reportTemplate;
    if (template != null) {
      if (contentType != null && !contentType.equalsIgnoreCase(
          MediaType.APPLICATION_XML_VALUE)) {
        return new ResponseEntity<>("Content Type not supported. Please set content type as "
            + MediaType.APPLICATION_XML_VALUE, HttpStatus.BAD_REQUEST);
      }
      try {
        reportTemplate = reportTemplateManager.fetchTemplate(template);
      } catch (IOException e) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(e),
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      if (templateId == null) {
        return new ResponseEntity<>("No template nor template id provided", HttpStatus.BAD_REQUEST);
      }
      reportTemplate = reportTemplateManager.getQualityReportTemplate(
          templateId);
    }
    reportGenerator.generate(reportTemplate);
    //TODO
    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
  }


}
