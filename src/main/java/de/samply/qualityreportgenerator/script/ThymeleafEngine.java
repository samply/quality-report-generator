package de.samply.qualityreportgenerator.script;

import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.context.Context;
import de.samply.qualityreportgenerator.template.script.Script;
import de.samply.qualityreportgenerator.template.script.ScriptFramework;
import java.io.FileWriter;
import java.io.IOException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class ThymeleafEngine extends ScriptEngineImpl {

  private TemplateEngine templateEngine = createTemplateEngine();

  @Override
  public ScriptFramework getScriptFramework() {
    return ScriptFramework.THYMELEAF;
  }

  @Override
  public ScriptResult generateRawResult(Script script, Context context)
      throws ScriptEngineException {
    ScriptResult result = super.generateRawResult(script, context);
    org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
    thymeleafContext.setVariable(QrgConst.THYMELEAF_CONTEXT_VARIABLE, context);
    generateResult(result, script, thymeleafContext);
    return result;
  }

  private void generateResult(ScriptResult result, Script script,
      org.thymeleaf.context.Context context) throws ScriptEngineException {
    try (FileWriter fileWriter = new FileWriter(result.getRawResult().toFile())) {
      templateEngine.process(script.getValue(), context, fileWriter);
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private TemplateEngine createTemplateEngine() {
    TemplateEngine templateEngine = new TemplateEngine();
    StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
    stringTemplateResolver.setTemplateMode(TemplateMode.TEXT);
    templateEngine.setTemplateResolver(stringTemplateResolver);
    return templateEngine;
  }

}
