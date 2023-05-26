package de.samply.reporter.template.script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptParser {

  public static String readTemplateAndParseScripts(Path templatePath) throws IOException {
    return readTemplateAndParseScripts(Files.readString(templatePath));
  }

  public static String readTemplateAndParseScripts(String template) throws IOException {
    AtomicReference<String> result = new AtomicReference(template);
    Arrays.stream(ScriptFramework.values()).forEach(scriptFramework ->
        result.set(result.get()
            .replace(scriptFramework.getStartTag(),
                "<script framework=\"" + scriptFramework.getFramework() + "\"><![CDATA[")
            .replace(scriptFramework.getEndTag(), "]]></script>")
        ));
    return replaceExporterTemplate(result.get());
  }

  private static String replaceExporterTemplate(String qualityReportTemplate) {
    if (qualityReportTemplate.contains("</exporter>")) {
      int index1 = qualityReportTemplate.indexOf("<exporter");
      int index2 = qualityReportTemplate.substring(index1).indexOf(">");
      int index3 = qualityReportTemplate.indexOf("</exporter>");
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(qualityReportTemplate.substring(0, index1 + index2 + 1));
      stringBuilder.append("<![CDATA[");
      stringBuilder.append(qualityReportTemplate.substring(index1 + index2 + 1, index3));
      stringBuilder.append("]]>");
      stringBuilder.append(qualityReportTemplate.substring(index3));
      qualityReportTemplate = stringBuilder.toString();
    }
    return qualityReportTemplate;
  }

}
