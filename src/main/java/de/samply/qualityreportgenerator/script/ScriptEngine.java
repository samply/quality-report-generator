package de.samply.qualityreportgenerator.script;

import de.samply.qualityreportgenerator.context.Context;
import de.samply.qualityreportgenerator.template.script.Script;
import de.samply.qualityreportgenerator.template.script.ScriptFramework;

public interface ScriptEngine {

  ScriptFramework getScriptFramework();
  ScriptResult generateRawResult(Script script, Context context) throws ScriptEngineException;

}
