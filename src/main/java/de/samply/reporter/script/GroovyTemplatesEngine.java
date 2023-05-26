package de.samply.reporter.script;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.Context;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import groovy.lang.Binding;
import groovy.text.GStringTemplateEngine;
import java.io.FileWriter;
import java.io.IOException;

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
    binding.setVariable(ReporterConst.THYMELEAF_CONTEXT_VARIABLE, context);
    generateResult(result, script, binding);
    removeUnnecessaryEmptyLines(result, context.getCsvConfig());
    return result;
  }

  private void generateResult(ScriptResult result, Script script, Binding binding)
      throws ScriptEngineException {
    try (FileWriter fileWriter = new FileWriter(result.getRawResult().toFile())) {
      engine.createTemplate(script.getValue()).make(binding.getVariables()).writeTo(fileWriter);
    } catch (ClassNotFoundException | IOException e) {
      throw new ScriptEngineException(e);
    }
  }

}
