package de.samply.reporter.report;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.context.ContextGenerator;
import de.samply.reporter.exporter.ExporterClient;
import de.samply.reporter.exporter.ExporterClientException;
import de.samply.reporter.script.ScriptEngineException;
import de.samply.reporter.script.ScriptEngineManager;
import de.samply.reporter.script.ScriptResult;
import de.samply.reporter.template.ColumnTemplate;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.SheetTemplate;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.utils.ExternalSheetUtils;
import de.samply.reporter.utils.ExternalSheetUtilsException;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
    addFormatToWorkbook(workbook, template);
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
        .forEach(sheetTemplate -> {
          if (sheetTemplate.getFileUrl() != null || sheetTemplate.getFilePath() != null) {
            addSheetFromSourceExcelFile(workbook, sheetTemplate);
          } else {
            fillSheetWithData(workbook, sheetTemplate, scriptResultMap);
          }
        });
  }

  private void addSheetFromSourceExcelFile(Workbook workbook, SheetTemplate template) {
    try {
      ExternalSheetUtils.addSheetFromSourceExcelFile(workbook, template);
    } catch (ExternalSheetUtilsException e) {
      throw new RuntimeException(e);
    }
  }

  private void fillSheetWithData(Workbook workbook, SheetTemplate template,
      Map<Script, ScriptResult> scriptResultMap) {
    Sheet sheet = createSheet(workbook, template);
    createHeaderRow(workbook, sheet, template);
    if (template.getValuesScript() != null) {
      ScriptResult result = scriptResultMap.get(template.getValuesScript().getScript());
      if (result != null) {
        fillSheetWithData(sheet, template, result);
        autoSizeSheet(sheet);
        addAutoFilter(sheet);
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

  private void createHeaderRow(Workbook workbook, Sheet sheet, SheetTemplate template) {
    Row row = sheet.createRow(0);
    AtomicInteger counter = new AtomicInteger(0);
    template.getColumnTemplates().forEach(
        columnTemplate -> row.createCell(counter.getAndIncrement())
            .setCellValue(columnTemplate.getName()));
    sheet.createFreezePane(0, 1);
    boldHeaderRow(workbook, row);
  }

  private void boldHeaderRow(Workbook workbook, Row titleRow) {
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    cellStyle.setFont(font);
    for (int j = 0; j < titleRow.getLastCellNum(); j++) {
      Cell cell = titleRow.getCell(j);
      cell.setCellStyle(cellStyle);
    }
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

  private void autoSizeSheet(Sheet sheet) {
    Row headerRow = sheet.getRow(0);
    if (headerRow != null) {
      for (int j = 0; j <= headerRow.getLastCellNum(); j++) {
        sheet.autoSizeColumn(j);
      }
    }
  }

  private void addAutoFilter(Sheet sheet) {
    int rowStartIndex = 0;
    int rowEndIndex = sheet.getLastRowNum();

    int columnStartIndex = 0;
    int columnEndIndex = sheet.getRow(0).getLastCellNum() - 1;

    CellRangeAddress cra = new CellRangeAddress(rowStartIndex, rowEndIndex, columnStartIndex,
        columnEndIndex);
    sheet.setAutoFilter(cra);
  }

  private void addFormatToWorkbook(Workbook workbook, ReportTemplate template) {
    template.getSheetTemplates().forEach(sheetTemplate -> {
      AtomicInteger columnNumber = new AtomicInteger(0);
      sheetTemplate.getColumnTemplates().forEach(
          columnTemplate -> addFormatToWorkbook(workbook, sheetTemplate, columnTemplate,
              columnNumber.getAndIncrement()));
    });
  }

  private void addFormatToWorkbook(Workbook workbook, SheetTemplate sheetTemplate,
      ColumnTemplate columnTemplate, int columnNumber) {
    Sheet sheet = workbook.getSheet(sheetTemplate.getName());
    addHeaderFormatToWorkbook(columnTemplate, sheet, columnNumber);
    addValueFormatToWorkbook(columnTemplate, sheet, columnNumber);
  }

  private void addHeaderFormatToWorkbook(ColumnTemplate template, Sheet sheet, int columnNumber) {
    if (template.getHeaderFormatScript() != null) {
      Script script = template.getHeaderFormatScript().getScript();
      if (script != null) {
        fetchCellContext(script, sheet.getWorkbook()).applyCellStyleToCell(
            sheet.getRow(0).getCell(columnNumber));
      }
    }
  }

  private void addValueFormatToWorkbook(ColumnTemplate template, Sheet sheet, int columnNumber) {
    if (template.getValueFormatScript() != null) {
      Script script = template.getValueFormatScript().getScript();
      if (script != null) {
        CellContext cellContext = fetchCellContext(script, sheet.getWorkbook());
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum > 0) {
          for (int i = 1; i <= lastRowNum; i++) {
            cellContext.applyCellStyleToCell(sheet.getRow(i).getCell(columnNumber));
          }
        }
      }
    }
  }

  private CellContext fetchCellContext(Script script, Workbook workbook) {
    try {
      return scriptEngineManager.generateCellContext(script, workbook);
    } catch (ScriptEngineException e) {
      throw new RuntimeException(e);
    }
  }

}
