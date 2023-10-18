package de.samply.reporter.script;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.CellStyleContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.context.CsvConfig;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.template.script.ScriptFramework;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ThymeleafEngine extends ScriptEngineImpl {

    private final TemplateEngine templateEngine = createTemplateEngine();

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
        generateResult(result, script, thymeleafContext, context.getCsvConfig());
        return result;
    }

    @Override
    public CellContext generateCellContext(Script script, CellStyleContext cellStyleContext,
                                           Context context) {
        CellContext cellContext = new CellContext(cellStyleContext);
        org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        thymeleafContext.setVariable(ReporterConst.CONTEXT_VARIABLE, context);
        thymeleafContext.setVariable(ReporterConst.CELL_CONTEXT_VARIABLE, cellStyleContext);
        generateResult(script, thymeleafContext, new StringWriter());
        return cellContext;
    }

    private void generateResult(
            ScriptResult result, Script script, org.thymeleaf.context.Context context, CsvConfig csvConfig) throws ScriptEngineException {
        try (FileWriter fileWriter = new FileWriter(result.rawResult().toFile(), csvConfig.charset())) {
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
