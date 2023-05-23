package de.samply.qualityreportgenerator.report;

import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.context.Context;
import de.samply.qualityreportgenerator.context.ContextGenerator;
import de.samply.qualityreportgenerator.exporter.ExporterClient;
import de.samply.qualityreportgenerator.exporter.ExporterClientException;
import de.samply.qualityreportgenerator.script.ScriptEngineManager;
import de.samply.qualityreportgenerator.script.ScriptResult;
import de.samply.qualityreportgenerator.template.QualityReportTemplate;
import de.samply.qualityreportgenerator.utils.VariablesReplacer;
import de.samply.qualityreportgenerator.zip.ExporterUnzipper;
import de.samply.qualityreportgenerator.zip.ExporterUnzipperException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QualityReportGenerator {

  private final ExporterClient exporterClient;
  private final ExporterUnzipper exporterUnzipper;
  private final VariablesReplacer variablesReplacer;
  private final ScriptEngineManager scriptEngineManager;
  private final ContextGenerator contextGenerator;
  private final Integer workbookWindow;
  private final Path qualityReportsDirectory;


  public QualityReportGenerator(
      ExporterClient exporterClient,
      ExporterUnzipper exporterUnzipper,
      VariablesReplacer variablesReplacer,
      ScriptEngineManager scriptEngineManager,
      ContextGenerator contextGenerator,
      @Value(QrgConst.EXCEL_WORKBOOK_WINDOW_SV) int workbookWindow,
      @Value(QrgConst.QUALITY_REPORTS_DIRECTORY_SV) String qualityReportsDirectory
  ) {
    this.exporterClient = exporterClient;
    this.exporterUnzipper = exporterUnzipper;
    this.variablesReplacer = variablesReplacer;
    this.scriptEngineManager = scriptEngineManager;
    this.contextGenerator = contextGenerator;
    this.workbookWindow = workbookWindow;
    this.qualityReportsDirectory = Path.of(qualityReportsDirectory);
  }

  public void generate(QualityReportTemplate template) throws QualityReportGeneratorException {
    try {
      exporterClient.fetchExportFiles(filePath -> generate(template, filePath), template);
    } catch (ExporterClientException | RuntimeException e) {
      throw new QualityReportGeneratorException(e);
    }
  }

  private void generate(QualityReportTemplate template, String filePath) {
    Path[] paths = extractPaths(filePath);
    Context context = contextGenerator.generate(template, paths);
    ScriptResult[] scriptResults = scriptEngineManager.generateRawQualityReport(template, context);
    // TODO
    Workbook workbook = new SXSSFWorkbook(workbookWindow);
    // TODO
    Path result = writeWorkbookAndGetQualityReportPath(workbook, template);
    // TODO
  }

  private Path[] extractPaths(String filePath) {
    try {
      return exporterUnzipper.extractFiles(filePath);
    } catch (ExporterUnzipperException e) {
      throw new RuntimeException(e);
    }
  }


  private Path writeWorkbookAndGetQualityReportPath(Workbook workbook,
      QualityReportTemplate template) {
    Path result = qualityReportsDirectory.resolve(
        variablesReplacer.fetchQualityReportFilename(template));
    writeWorkbook(result, workbook);
    return result;
  }

  private void writeWorkbook(Path path, Workbook workbook) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
      workbook.write(fileOutputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
