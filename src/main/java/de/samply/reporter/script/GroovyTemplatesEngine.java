package de.samply.reporter.script;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import groovy.lang.Binding;
import groovy.text.GStringTemplateEngine;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.poi.ss.usermodel.Workbook;

public class GroovyTemplatesEngine extends ScriptEngineImpl {

  private GStringTemplateEngine engine = new GStringTemplateEngine();

  @Override
  public ScriptFramework getScriptFramework() {
    return ScriptFramework.GROOVY_TEMPLATES;
  }

  @Override
  public ScriptResult generateRawResult(Script script, Context context)
      throws ScriptEngineException {
    ScriptResult result = super.generateRawResult(script, context);
    Binding binding = new Binding();
    binding.setVariable(ReporterConst.CONTEXT_VARIABLE, context);
    generateResult(result, script, binding);
    removeUnnecessaryEmptyLines(result, context.getCsvConfig());
    return result;
  }

  private void generateResult(ScriptResult result, Script script, Binding binding)
      throws ScriptEngineException {
    try (FileWriter fileWriter = new FileWriter(result.getRawResult().toFile())) {
      generateResult(script, binding, fileWriter);
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private void generateResult(Script script, Binding binding, Writer writer)
      throws ScriptEngineException {
    try {
      engine.createTemplate(script.getValue()).make(binding.getVariables()).writeTo(writer);
    } catch (ClassNotFoundException | IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  @Override
  public CellContext generateCellContext(Script script, Workbook workbook) throws ScriptEngineException {
    CellContext result = new CellContext(workbook);
    Binding binding = new Binding();
    binding.setVariable(ReporterConst.CELL_CONTEXT_VARIABLE, result);
    generateResult(script, binding, new StringWriter());
    return result;
  }

}
