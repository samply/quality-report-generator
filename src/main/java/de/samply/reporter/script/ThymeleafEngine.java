package de.samply.reporter.script;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.poi.ss.usermodel.Workbook;
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
    thymeleafContext.setVariable(ReporterConst.CONTEXT_VARIABLE, context);
    generateResult(result, script, thymeleafContext);
    return result;
  }

  @Override
  public CellContext generateCellContext(Script script, Workbook workbook)
      throws ScriptEngineException {
    CellContext cellContext = new CellContext(workbook);
    org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
    thymeleafContext.setVariable(ReporterConst.CONTEXT_VARIABLE, cellContext);
    generateResult(script, thymeleafContext, new StringWriter());
    return cellContext;
  }

  private void generateResult(ScriptResult result, Script script,
      org.thymeleaf.context.Context context) throws ScriptEngineException {
    try (FileWriter fileWriter = new FileWriter(result.getRawResult().toFile())) {
      generateResult(script, context, fileWriter);
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private void generateResult(Script script, org.thymeleaf.context.Context context, Writer writer) {
    templateEngine.process(script.getValue(), context, writer);
  }

  private TemplateEngine createTemplateEngine() {
    TemplateEngine templateEngine = new TemplateEngine();
    StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
    stringTemplateResolver.setTemplateMode(TemplateMode.TEXT);
    templateEngine.setTemplateResolver(stringTemplateResolver);
    return templateEngine;
  }

}
