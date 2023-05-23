package de.samply.qualityreportgenerator.script;

import de.samply.qualityreportgenerator.context.Context;
import de.samply.qualityreportgenerator.template.QualityReportTemplate;
import de.samply.qualityreportgenerator.template.script.Script;
import de.samply.qualityreportgenerator.template.script.ScriptFramework;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ScriptEngineManager {

  private ScriptEngine[] scriptEngines = {new ThymeleafEngine(), new GroovyTemplatesEngine()};
  private Map<ScriptFramework, ScriptEngine> idScriptEngineMap = new HashMap<>();

  public ScriptEngineManager() {
    Arrays.stream(scriptEngines).forEach(
        scriptEngine -> idScriptEngineMap.put(scriptEngine.getScriptFramework(), scriptEngine));
  }

  public ScriptResult[] generateRawQualityReport(QualityReportTemplate template,
      Context context) {
    List<ScriptResult> results = new ArrayList<>();
    template.getSheetTemplates().forEach(sheetTemplate -> {
      if (sheetTemplate.getValuesScript() != null) {
        results.add(generateRawResult(sheetTemplate.getValuesScript().getScript(), context));
      }
    });
    return results.toArray(new ScriptResult[0]);
  }

  private ScriptResult generateRawResult(Script script, Context context) {
    try {
      return generateRawResultWithoutExceptionHandling(script, context);
    } catch (ScriptEngineException e) {
      throw new RuntimeException(e);
    }
  }

  private ScriptResult generateRawResultWithoutExceptionHandling(Script script,
      Context context)
      throws ScriptEngineException {
    ScriptFramework scriptFramework =
        (script.getFramework() != null) ? ScriptFramework.valueOfFramework(script.getFramework())
            : ScriptFramework.getDefault();
    if (scriptFramework == null) {
      throw new RuntimeException("Script Framework " + script.getFramework() + " not found");
    }
    return idScriptEngineMap.get(scriptFramework).generateRawResult(script, context);
  }

}
