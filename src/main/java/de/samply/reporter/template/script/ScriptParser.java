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

  public static String readTemplateAndParseScripts(String template) {
    template = prepareTemplateForEmbeddedScripts(template);
    return replaceExporterTemplate(template);
  }

  private static String prepareTemplateForEmbeddedScripts (String template){
    AtomicReference<String> result = new AtomicReference<>(template);
    Arrays.stream(ScriptFramework.values()).forEach(scriptFramework ->
            result.set(result.get()
                    .replace(scriptFramework.getStartTag(),
                            "<script framework=\"" + scriptFramework.getFramework() + "\"><![CDATA[")
                    .replace(scriptFramework.getEndTag(), "]]></script>")
            ));
    return result.get();
  }

  private static String replaceExporterTemplate(String template) {
    if (template.contains("</exporter>")) {
      int index1 = template.indexOf("<exporter");
      int index2 = template.substring(index1).indexOf(">");
      int index3 = template.indexOf("</exporter>");
      template = template.substring(0, index1 + index2 + 1)
          + "<![CDATA["
          + template.substring(index1 + index2 + 1, index3)
          + "]]>"
          + template.substring(index3);
    }
    return template;
  }

}
