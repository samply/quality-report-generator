package de.samply.qualityreportgenerator.app;

import de.samply.qualityreportgenerator.report.QualityReportGenerator;
import de.samply.qualityreportgenerator.report.QualityReportGeneratorException;
import de.samply.qualityreportgenerator.template.QualityReportTemplate;
import de.samply.qualityreportgenerator.template.QualityReportTemplateManager;
import de.samply.qualityreportgenerator.utils.ProjectVersion;
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
public class QrgController {

  private final String projectVersion = ProjectVersion.getProjectVersion();
  private QualityReportGenerator qualityReportGenerator;
  private QualityReportTemplateManager qualityReportTemplateManager;

  public QrgController(
      QualityReportGenerator qualityReportGenerator,
      QualityReportTemplateManager qualityReportTemplateManager) {
    this.qualityReportGenerator = qualityReportGenerator;
    this.qualityReportTemplateManager = qualityReportTemplateManager;
  }

  //@CrossOrigin(origins = "${CROSS_ORIGINS}", allowedHeaders = {"Authorization"})
  @GetMapping(value = QrgConst.INFO)
  public ResponseEntity<String> info() {
    return new ResponseEntity<>(projectVersion, HttpStatus.OK);
  }

  @GetMapping(value = QrgConst.GENERATE)
  public ResponseEntity<String> generate(
      @RequestParam(name = QrgConst.QUALITY_REPORT_TEMPLATE_ID, required = false) String templateId,
      @RequestHeader(name = "Content-Type", required = false) String contentType,
      @RequestBody(required = false) String template
  ) throws QualityReportGeneratorException {
    QualityReportTemplate qualityReportTemplate;
    if (template != null) {
      if (contentType != null && !contentType.equalsIgnoreCase(
          MediaType.APPLICATION_XML_VALUE)) {
        return new ResponseEntity<>("Content Type not supported. Please set content type as "
            + MediaType.APPLICATION_XML_VALUE, HttpStatus.BAD_REQUEST);
      }
      try {
        qualityReportTemplate = qualityReportTemplateManager.fetchTemplate(template);
      } catch (IOException e) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(e),
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      if (templateId == null) {
        return new ResponseEntity<>("No template nor template id provided", HttpStatus.BAD_REQUEST);
      }
      qualityReportTemplate = qualityReportTemplateManager.getQualityReportTemplate(
          templateId);
    }
    qualityReportGenerator.generate(qualityReportTemplate);
    //TODO
    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
  }


}
