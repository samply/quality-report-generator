package de.samply.reporter.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.reporter.report.ReportGenerator;
import de.samply.reporter.report.ReportGeneratorException;
import de.samply.reporter.report.metainfo.ReportMetaInfo;
import de.samply.reporter.report.metainfo.ReportMetaInfoManager;
import de.samply.reporter.report.metainfo.ReportMetaInfoManagerException;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.ReportTemplateManager;
import de.samply.reporter.utils.ProjectVersion;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ReporterController {

  private final String projectVersion = ProjectVersion.getProjectVersion();
  private final ReportGenerator reportGenerator;
  private final ReportMetaInfoManager reportMetaInfoManager;
  private final ReportTemplateManager reportTemplateManager;
  private final String httpRelativePath;
  private final String httpServletRequestScheme;
  private final ObjectMapper objectMapper = new ObjectMapper();


  public ReporterController(
      @Value(ReporterConst.HTTP_RELATIVE_PATH_SV) String httpRelativePath,
      @Value(ReporterConst.HTTP_SERVLET_REQUEST_SCHEME_SV) String httpServletRequestScheme,
      ReportGenerator reportGenerator,
      ReportTemplateManager reportTemplateManager,
      ReportMetaInfoManager reportMetaInfoManager) {
    this.httpRelativePath = httpRelativePath;
    this.httpServletRequestScheme = httpServletRequestScheme;
    this.reportGenerator = reportGenerator;
    this.reportTemplateManager = reportTemplateManager;
    this.reportMetaInfoManager = reportMetaInfoManager;
  }

  //@CrossOrigin(origins = "${CROSS_ORIGINS}", allowedHeaders = {"Authorization"})
  @GetMapping(value = ReporterConst.INFO)
  public ResponseEntity<String> info() {
    return new ResponseEntity<>(projectVersion, HttpStatus.OK);
  }

  @PostMapping(value = ReporterConst.GENERATE)
  public ResponseEntity<String> generate(
      HttpServletRequest httpServletRequest,
      @RequestParam(name = ReporterConst.REPORT_TEMPLATE_ID, required = false) String templateId,
      @RequestHeader(name = "Content-Type", required = false) String contentType,
      @RequestBody(required = false) String template
  ) throws ReportGeneratorException, ReportMetaInfoManagerException, JsonProcessingException {
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
      reportTemplate = reportTemplateManager.getQualityReportTemplate(templateId);
    }
    ReportMetaInfo reportMetaInfo = reportMetaInfoManager.createNewReportMetaInfo(
        reportTemplate);
    reportGenerator.generate(reportTemplate, reportMetaInfo);
    return new ResponseEntity<>(
        createRequestResponseEntity(httpServletRequest, reportMetaInfo.id()), HttpStatus.OK);
  }

  private String createRequestResponseEntity(HttpServletRequest request, String reportId)
      throws JsonProcessingException {
    return objectMapper.writeValueAsString(
        new GenerateResponseEntity(fetchResponseUrl(request, reportId)));
  }

  private String fetchResponseUrl(HttpServletRequest httpServletRequest, String reportId) {
    return ServletUriComponentsBuilder.fromRequestUri(httpServletRequest)
        .scheme(httpServletRequestScheme)
        .replacePath(createHttpPath(ReporterConst.REPORT))
        .queryParam(ReporterConst.REPORT_ID, reportId).toUriString();
  }

  private String createHttpPath(String httpPath) {
    return (httpRelativePath != null && httpRelativePath.length() > 0) ? httpRelativePath + '/'
        + httpPath : httpPath;
  }

  @GetMapping(value = ReporterConst.REPORT, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<InputStreamResource> fetchReport(
      @RequestParam(name = ReporterConst.REPORT_ID) String reportId
  ) throws ReportMetaInfoManagerException, FileNotFoundException {
    Optional<ReportMetaInfo> reportMetaInfo = reportMetaInfoManager.fetchReportMetaInfo(reportId);
    if (reportMetaInfo.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    if (!reportMetaInfo.get().path().toFile().exists()) {
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    return createResponseEntity(reportMetaInfo.get().path());
  }

  private ResponseEntity<InputStreamResource> createResponseEntity(Path path)
      throws FileNotFoundException {
    return createResponseEntity(new InputStreamResource(new FileInputStream(path.toFile())),
        path.getFileName().toString());
  }

  private ResponseEntity<InputStreamResource> createResponseEntity(
      InputStreamResource inputStreamResource, String filename) {
    return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + filename)
        .body(inputStreamResource);
  }

  @GetMapping(value = ReporterConst.REPORTS_LIST)
  public ResponseEntity fetchAllReports() throws ReportMetaInfoManagerException {
    return ResponseEntity.ok().body(reportMetaInfoManager.fetchAllExistingReportMetaInfos());
  }

  @GetMapping(value = ReporterConst.REPORT_STATUS)
  public ResponseEntity<ReportStatus> fetchReportStatus(
      @RequestParam(name = ReporterConst.REPORT_ID) String reportId
  ) throws ReportMetaInfoManagerException {
    Optional<ReportMetaInfo> reportMetaInfo = reportMetaInfoManager.fetchReportMetaInfo(reportId);
    if (reportMetaInfo.isEmpty()) {
      return ResponseEntity.ok(ReportStatus.NOT_FOUND);
    }
    if (!reportMetaInfo.get().path().toFile().exists()) {
      return ResponseEntity.ok(ReportStatus.RUNNING);
    }
    return ResponseEntity.ok(ReportStatus.OK);
  }


}
