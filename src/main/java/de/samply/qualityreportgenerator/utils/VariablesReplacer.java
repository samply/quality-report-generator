package de.samply.qualityreportgenerator.utils;

import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.template.QualityReportTemplate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VariablesReplacer {

  private final EnvironmentUtils environmentUtils;
  private final String qualityReportFilenameTemplate;
  private final String timestampFormat;

  public VariablesReplacer(
      EnvironmentUtils environmentUtils,
      @Value(QrgConst.QUALITY_REPORT_FILENAME_TEMPLATE_SV) String qualityReportFilenameTemplate,
      @Value(QrgConst.TIMESTAMP_FORMAT_SV) String timestampFormat
  ) {
    this.environmentUtils = environmentUtils;
    this.qualityReportFilenameTemplate = qualityReportFilenameTemplate;
    this.timestampFormat = timestampFormat;
  }

  public String fetchQualityReportFilename(QualityReportTemplate template) {
    String result =
        (template.getFilename() != null) ? template.getFilename() : qualityReportFilenameTemplate;
    result = replaceAllVariables(result);
    return result;
  }

  public String replaceAllVariables(String input){
    input = replaceTimestamp(input);
    input = replaceVariables(input);
    return input;
  }

  private String replaceTimestamp(String filename) {
    if (filename.contains(QrgConst.TEMPLATE_TIMESTAMP)) {
      String timestampFormat = this.timestampFormat;
      String templateStart = QrgConst.TEMPLATE_TIMESTAMP + QrgConst.TEMPLATE_SEPARATOR;
      if (filename.contains(templateStart)) {
        int index1 = filename.indexOf(templateStart);
        int index2 = filename.substring(index1).indexOf(QrgConst.TEMPLATE_END);
        timestampFormat = filename.substring(index1 + templateStart.length(), index1 + index2);
        filename = filename.replace(QrgConst.TEMPLATE_SEPARATOR + timestampFormat, "");
      }
      String timestamp = fetchTimestamp(timestampFormat);
      filename = filename.replace(
          QrgConst.TEMPLATE_START + QrgConst.TEMPLATE_TIMESTAMP + QrgConst.TEMPLATE_END, timestamp);
    }
    return filename;
  }

  private String replaceVariables(String filename) {
    while (filename.contains(QrgConst.TEMPLATE_START)) {
      String variable = fetchNextVariable(filename);
      String value = environmentUtils.getEnvironmentVariable(variable);
      filename = filename.replace(QrgConst.TEMPLATE_START + variable + QrgConst.TEMPLATE_END,
          (value != null) ? value : "");
    }
    return filename;
  }

  private String fetchNextVariable(String filename) {
    int index1 = filename.indexOf(QrgConst.TEMPLATE_START);
    int index2 = filename.substring(index1).indexOf(QrgConst.TEMPLATE_END);
    return (index2 >= 0) ?
        filename.substring(index1 + QrgConst.TEMPLATE_START.length(), index1 + index2)
        : filename.substring(index1 + QrgConst.TEMPLATE_START.length());
  }

  private String fetchTimestamp(String format) {
    if (format == null) {
      format = timestampFormat;
    }
    return new SimpleDateFormat(format).format(Timestamp.from(Instant.now()));
  }


}
