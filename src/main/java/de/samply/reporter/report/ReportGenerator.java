package de.samply.reporter.report;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.CellContext;
import de.samply.reporter.context.CellStyleContext;
import de.samply.reporter.context.Context;
import de.samply.reporter.context.ContextGenerator;
import de.samply.reporter.exporter.ExporterClient;
import de.samply.reporter.exporter.ExporterClientException;
import de.samply.reporter.report.metainfo.ReportMetaInfo;
import de.samply.reporter.script.ScriptEngineException;
import de.samply.reporter.script.ScriptEngineManager;
import de.samply.reporter.script.ScriptResult;
import de.samply.reporter.template.ColumnTemplate;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.template.SheetTemplate;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.utils.ExternalSheetUtils;
import de.samply.reporter.utils.ExternalSheetUtilsException;
import de.samply.reporter.utils.FileUtils;
import de.samply.reporter.utils.PercentageLogger;
import de.samply.reporter.zip.ExporterUnzipper;
import de.samply.reporter.zip.ExporterUnzipperException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

@Component
public class ReportGenerator {

  private final static Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
  private final ExporterClient exporterClient;
  private final ExporterUnzipper exporterUnzipper;
  private final ScriptEngineManager scriptEngineManager;
  private final ContextGenerator contextGenerator;
  private final Integer workbookWindow;


  public ReportGenerator(
      ExporterClient exporterClient,
      ExporterUnzipper exporterUnzipper,
      ScriptEngineManager scriptEngineManager,
      ContextGenerator contextGenerator,
      @Value(ReporterConst.EXCEL_WORKBOOK_WINDOW_SV) int workbookWindow
  ) {
    this.exporterClient = exporterClient;
    this.exporterUnzipper = exporterUnzipper;
    this.scriptEngineManager = scriptEngineManager;
    this.contextGenerator = contextGenerator;
    this.workbookWindow = workbookWindow;
  }

  public void generate(ReportTemplate template, ReportMetaInfo reportMetaInfo)
      throws ReportGeneratorException {
    try {
      exporterClient.fetchExportFiles(filePath -> generate(template, filePath, reportMetaInfo),
          template);
    } catch (ExporterClientException | RuntimeException e) {
      throw new ReportGeneratorException(e);
    }
  }

  private void generate(ReportTemplate template, String filePath, ReportMetaInfo reportMetaInfo) {
    logger.info("Extracting paths...");
    Path[] paths = extractPaths(filePath);
    logger.info("Generating context...");
    Context context = contextGenerator.generate(template, paths);
    logger.info("Generating raw report...");
    Map<Script, ScriptResult> scriptResultMap = scriptEngineManager.generateRawReport(
        template, context);
    logger.info("Generating excel file");
    Workbook workbook = new SXSSFWorkbook(workbookWindow);
    logger.info("Filling excel file with data...");
    fillWorkbookWithData(workbook, template, scriptResultMap);
    logger.info("Adding format to excel file...");
    addFormatToWorkbook(workbook, template, context);
    logger.info("Writing excel file...");
    writeWorkbook(reportMetaInfo.path(), workbook);
    logger.info("Removing temporal files...");
    removeTemporalFiles(paths[0].getParent(), scriptResultMap.values());
    logger.info("Excel file generated satisfactory.");
  }

  private void removeTemporalFiles(Path sourceFilesDirectory,
      Collection<ScriptResult> scriptResults) {
    try {
      removeTemporalFilesWithoutExceptionHandling(sourceFilesDirectory, scriptResults);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void removeTemporalFilesWithoutExceptionHandling(Path sourceFilesDirectory,
      Collection<ScriptResult> scriptResults)
      throws IOException {
    FileSystemUtils.deleteRecursively(sourceFilesDirectory);
    scriptResults.forEach(scriptResult -> {
      try {
        Files.delete(scriptResult.getRawResult());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private Path[] extractPaths(String filePath) {
    try {
      return exporterUnzipper.extractFiles(filePath);
    } catch (ExporterUnzipperException e) {
      throw new RuntimeException(e);
    }
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
          logger.info("Adding sheet " + sheetTemplate.getName() + "...");
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
        fillSheetWithData(sheet, result);
        logger.info("Adding autosize to sheet " + template.getName() + "...");
        autoSizeSheet(sheet);
        logger.info("Adding auto filter to sheet " + template.getName() + "...");
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
    if (!template.getColumnTemplates().isEmpty()) {
      logger.info("Creating header row for sheet " + template.getName() + "...");
      Row row = sheet.createRow(0);
      AtomicInteger counter = new AtomicInteger(0);
      template.getColumnTemplates().forEach(
          columnTemplate -> row.createCell(counter.getAndIncrement())
              .setCellValue(columnTemplate.getName()));
      sheet.createFreezePane(0, 1);
      boldHeaderRow(workbook, row);
    }
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

  private void fillSheetWithData(Sheet sheet, ScriptResult result) {
    try {
      fillSheetWithDataWithoutExceptionHandling(sheet, result);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void fillSheetWithDataWithoutExceptionHandling(Sheet sheet, ScriptResult result)
      throws IOException {
    AtomicInteger rowIndex = new AtomicInteger(sheet.getLastRowNum() + 1);
    PercentageLogger percentageLogger = new PercentageLogger(logger,
        (int) FileUtils.fetchNumberOfLines(result.getRawResult()),
        "Filling sheet" + sheet.getSheetName() + " with data...");
    Files.readAllLines(result.getRawResult())
        .forEach(
            line -> {
              fillRowWithData(sheet.createRow(rowIndex.getAndIncrement()), line, result);
              percentageLogger.incrementCounter();
            });
  }

  private void fillRowWithData(Row row, String line, ScriptResult result) {
    AtomicInteger columnIndex = new AtomicInteger(0);
    Arrays.stream(line.split(result.getCsvConfig().delimiter())).forEach(
        value -> row.createCell(columnIndex.getAndIncrement())
            .setCellValue((value != null) ? value : ReporterConst.EMPTY_EXCEL_CELL));
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

  private void addFormatToWorkbook(Workbook workbook, ReportTemplate template, Context context) {
    template.getSheetTemplates().forEach(sheetTemplate -> {
      CellStyleContext cellStyleContext = new CellStyleContext(workbook);
      AtomicInteger columnNumber = new AtomicInteger(0);
      logger.info("Adding general format to all cells...");
      sheetTemplate.getFormatScripts().forEach(formatScript -> {
        if (formatScript.getScript() != null) {
          addFormatToAllCellsOfASheet(workbook, sheetTemplate, cellStyleContext, context,
              formatScript.getScript());
        }
      });
      logger.info("Adding column format to column cells...");
      sheetTemplate.getColumnTemplates().forEach(
          columnTemplate -> addFormatToWorkbook(workbook, sheetTemplate, columnTemplate,
              columnNumber.getAndIncrement(), cellStyleContext, context));
    });
  }

  private void addFormatToWorkbook(Workbook workbook, SheetTemplate sheetTemplate,
      ColumnTemplate columnTemplate, int columnNumber, CellStyleContext cellStyleContext,
      Context context) {
    Sheet sheet = workbook.getSheet(sheetTemplate.getName());
    logger.info("Adding header format to column " + columnTemplate.getName() + " in sheet "
        + sheetTemplate.getName() + "...");
    addHeaderFormatToWorkbook(columnTemplate, sheet, columnNumber, cellStyleContext, context);
    logger.info("Adding value format to column " + columnTemplate.getName() + " in sheet "
        + sheetTemplate.getName() + "...");
    addValueFormatToWorkbook(columnTemplate, sheet, columnNumber, cellStyleContext, context);
  }

  private void addHeaderFormatToWorkbook(ColumnTemplate template, Sheet sheet, int columnNumber,
      CellStyleContext cellStyleContext, Context context) {
    if (template.getHeaderFormatScript() != null) {
      Script script = template.getHeaderFormatScript().getScript();
      if (script != null) {
        fetchCellContext(script, cellStyleContext, context).applyCellStyleToCell(
            sheet.getRow(0).getCell(columnNumber));
      }
    }
  }

  private void addValueFormatToWorkbook(ColumnTemplate template, Sheet sheet, int columnNumber,
      CellStyleContext cellStyleContext, Context context) {
    if (template.getValueFormatScript() != null) {
      Script script = template.getValueFormatScript().getScript();
      if (script != null) {
        CellContext cellContext = fetchCellContext(script, cellStyleContext, context);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum > 0) {
          for (int i = 1; i <= lastRowNum; i++) {
            cellContext.applyCellStyleToCell(sheet.getRow(i).getCell(columnNumber));
          }
        }
      }
    }
  }

  private void addFormatToAllCellsOfASheet(Workbook workbook, SheetTemplate sheetTemplate,
      CellStyleContext cellStyleContext, Context context, Script script) {
    CellContext cellContext = fetchCellContext(script, cellStyleContext, context);
    Sheet sheet = workbook.getSheet(sheetTemplate.getName());
    PercentageLogger percentageLogger = new PercentageLogger(logger, sheet.getLastRowNum(),
        "Adding format to all cells of sheet " + sheetTemplate.getName() + "...");
    sheet.forEach(row -> {
      row.forEach(cellContext::applyCellStyleToCell);
      percentageLogger.incrementCounter();
    });
  }

  private CellContext fetchCellContext(Script script, CellStyleContext cellStyleContext,
      Context context) {
    try {
      return scriptEngineManager.generateCellContext(script, cellStyleContext, context);
    } catch (ScriptEngineException e) {
      throw new RuntimeException(e);
    }
  }

}
