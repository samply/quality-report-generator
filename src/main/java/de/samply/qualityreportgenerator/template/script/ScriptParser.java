package de.samply.qualityreportgenerator.template.script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptParser {

  public static String readTemplateAndParseScripts(Path templatePath) throws IOException {
    AtomicReference<String> result = new AtomicReference(Files.readString(templatePath));
    Arrays.stream(ScriptFramework.values()).forEach(scriptFramework ->
        result.set(result.get()
            .replace(scriptFramework.getStartTag(),
                "<script framework=\"" + scriptFramework.getFramework() + "\"><![CDATA[")
            .replace(scriptFramework.getEndTag(), "]]></script>")
        ));
    return result.get();
  }

}
