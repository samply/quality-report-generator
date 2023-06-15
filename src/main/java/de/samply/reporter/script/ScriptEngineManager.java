package de.samply.reporter.script;

import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.CellStyleContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import de.samply.reporter.template.script.ScriptReference;
import java.util.Arrays;
import java.util.HashMap;
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

  public Map<Script, ScriptResult> generateRawQualityReport(ReportTemplate template,
      Context context) {
    Map<Script, ScriptResult> results = new HashMap<>();
    addScriptToResultsAndGenerateRawResult(template.getInitScript(), results, context);
    template.getSheetTemplates().forEach(sheetTemplate -> {
      addScriptToResultsAndGenerateRawResult(sheetTemplate.getValuesScript(), results, context);
    });
    return results;
  }

  private void addScriptToResultsAndGenerateRawResult(ScriptReference scriptReference,
      Map<Script, ScriptResult> results, Context context) {
    if (scriptReference != null) {
      Script script = scriptReference.getScript();
      results.put(script, generateRawResult(script, context));
    }
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
    return idScriptEngineMap.get(fetchScriptFramework(script)).generateRawResult(script, context);
  }

  private ScriptFramework fetchScriptFramework(Script script) {
    ScriptFramework scriptFramework =
        (script.getFramework() != null) ? ScriptFramework.valueOfFramework(script.getFramework())
            : ScriptFramework.getDefault();
    if (scriptFramework == null) {
      throw new RuntimeException("Script Framework " + script.getFramework() + " not found");
    }
    return scriptFramework;
  }

  public CellContext generateCellContext(Script script, CellStyleContext cellStyleContext, Context context)
      throws ScriptEngineException {
    return idScriptEngineMap.get(fetchScriptFramework(script))
        .generateCellContext(script, cellStyleContext, context);
  }

}
