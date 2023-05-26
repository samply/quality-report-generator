package de.samply.reporter.script;

import de.samply.reporter.context.Context;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;

public interface ScriptEngine {

  ScriptFramework getScriptFramework();
  ScriptResult generateRawResult(Script script, Context context) throws ScriptEngineException;

}
