package de.samply.reporter.report;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.Context;
import de.samply.reporter.context.ContextGenerator;
import de.samply.reporter.exporter.ExporterClient;
import de.samply.reporter.exporter.ExporterClientException;
import de.samply.reporter.script.ScriptEngineManager;
import de.samply.reporter.script.ScriptResult;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.SheetTemplate;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.utils.VariablesReplacer;
import de.samply.reporter.zip.ExporterUnzipper;
import de.samply.reporter.zip.ExporterUnzipperException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportGenerator {

  private final ExporterClient exporterClient;
  private final ExporterUnzipper exporterUnzipper;
  private final VariablesReplacer variablesReplacer;
  private final ScriptEngineManager scriptEngineManager;
  private final ContextGenerator contextGenerator;
  private final Integer workbookWindow;
  private final Path qualityReportsDirectory;


  public ReportGenerator(
      ExporterClient exporterClient,
      ExporterUnzipper exporterUnzipper,
      VariablesReplacer variablesReplacer,
      ScriptEngineManager scriptEngineManager,
      ContextGenerator contextGenerator,
      @Value(ReporterConst.EXCEL_WORKBOOK_WINDOW_SV) int workbookWindow,
      @Value(ReporterConst.REPORTS_DIRECTORY_SV) String qualityReportsDirectory
  ) {
    this.exporterClient = exporterClient;
    this.exporterUnzipper = exporterUnzipper;
    this.variablesReplacer = variablesReplacer;
    this.scriptEngineManager = scriptEngineManager;
    this.contextGenerator = contextGenerator;
    this.workbookWindow = workbookWindow;
    this.qualityReportsDirectory = Path.of(qualityReportsDirectory);
  }

  public void generate(ReportTemplate template) throws ReportGeneratorException {
    try {
      exporterClient.fetchExportFiles(filePath -> generate(template, filePath), template);
    } catch (ExporterClientException | RuntimeException e) {
      throw new ReportGeneratorException(e);
    }
  }

  private void generate(ReportTemplate template, String filePath) {
    Path[] paths = extractPaths(filePath);
    Context context = contextGenerator.generate(template, paths);
    Map<Script, ScriptResult> scriptResultMap = scriptEngineManager.generateRawQualityReport(
        template, context);
    Workbook workbook = new SXSSFWorkbook(workbookWindow);
    fillWorkbookWithData(workbook, template, scriptResultMap);
    Path result = writeWorkbookAndGetQualityReportPath(workbook, template);
    // TODO: Remove temporal files
  }

  private Path[] extractPaths(String filePath) {
    try {
      return exporterUnzipper.extractFiles(filePath);
    } catch (ExporterUnzipperException e) {
      throw new RuntimeException(e);
    }
  }


  private Path writeWorkbookAndGetQualityReportPath(Workbook workbook,
      ReportTemplate template) {
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

  private void fillWorkbookWithData(Workbook workbook, ReportTemplate template,
      Map<Script, ScriptResult> scriptResultMap) {
    template.getSheetTemplates()
        .forEach(sheetTemplate -> fillSheetWithData(workbook, sheetTemplate, scriptResultMap));
  }

  private void fillSheetWithData(Workbook workbook, SheetTemplate template,
      Map<Script, ScriptResult> scriptResultMap) {
    Sheet sheet = createSheet(workbook, template);
    createHeaderRow(sheet, template);
    if (template.getValuesScript() != null) {
      ScriptResult result = scriptResultMap.get(template.getValuesScript().getScript());
      if (result != null) {
        fillSheetWithData(sheet, template, result);
      }
    }
  }

  private Sheet createSheet(Workbook workbook, SheetTemplate template) {
    Sheet sheet = workbook.createSheet(template.getName());
    if (sheet instanceof SXSSFSheet) {
      ((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
    }
    return sheet;
  }

  private void createHeaderRow(Sheet sheet, SheetTemplate template) {
    Row row = sheet.createRow(0);
    AtomicInteger counter = new AtomicInteger(0);
    template.getColumnTemplates().forEach(
        columnTemplate -> row.createCell(counter.getAndIncrement())
            .setCellValue(columnTemplate.getName()));
  }

  private void fillSheetWithData(Sheet sheet, SheetTemplate template, ScriptResult result) {
    try {
      fillSheetWithDataWithoutExceptionHandling(sheet, template, result);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void fillSheetWithDataWithoutExceptionHandling(Sheet sheet, SheetTemplate template,
      ScriptResult result)
      throws IOException {
    AtomicInteger rowIndex = new AtomicInteger(sheet.getLastRowNum() + 1);
    Files.readAllLines(result.getRawResult())
        .forEach(
            line -> fillRowWithData(sheet.createRow(rowIndex.getAndIncrement()), line, result));
  }

  private void fillRowWithData(Row row, String line, ScriptResult result) {
    AtomicInteger columnIndex = new AtomicInteger(0);
    Arrays.stream(line.split(result.getCsvConfig().delimiter())).forEach(value -> {
      row.createCell(columnIndex.getAndIncrement())
          .setCellValue((value != null) ? value : ReporterConst.EMPTY_EXCEL_CELL);
    });
  }


}
