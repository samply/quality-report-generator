package de.samply.reporter.script;

import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.CellStyleContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.logger.BufferedLoggerFactory;
import de.samply.reporter.logger.Logger;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import de.samply.reporter.template.script.ScriptReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class ScriptEngineManager {

    private final Logger logger = BufferedLoggerFactory.getLogger(ScriptEngineManager.class);
    private final Map<ScriptFramework, ScriptEngine> idScriptEngineMap = new HashMap<>();

    public ScriptEngineManager() {
        ScriptEngine[] scriptEngines = {new ThymeleafEngine(), new GroovyTemplatesEngine()};
        Arrays.stream(scriptEngines).forEach(
                scriptEngine -> idScriptEngineMap.put(scriptEngine.getScriptFramework(), scriptEngine));
    }

    public Map<Script, ScriptResult> generateRawReport(ReportTemplate template,
                                                       Context context) {
        Map<Script, ScriptResult> results = new HashMap<>();
        addScriptToResultsAndGenerateRawResult(template.getInitScript(), results, context);
        template.getSheetTemplates().forEach(sheetTemplate -> {
            logger.info(
                    "Generating temporal file with raw data for template " + sheetTemplate.getName() + "...");
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

    public CellContext generateCellContext(Script script, CellStyleContext cellStyleContext,
                                           Context context)
            throws ScriptEngineException {
        return idScriptEngineMap.get(fetchScriptFramework(script))
                .generateCellContext(script, cellStyleContext, context);
    }

}
