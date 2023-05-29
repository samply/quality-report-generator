package de.samply.reporter.script;

import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import org.apache.poi.ss.usermodel.Workbook;

public interface ScriptEngine {

  ScriptFramework getScriptFramework();

  ScriptResult generateRawResult(Script script, Context context) throws ScriptEngineException;

  CellContext generateCellContext(Script script, Workbook workbook) throws ScriptEngineException;

}
