package de.samply.reporter.report.workbook;

import de.samply.reporter.logger.BufferedLoggerFactory;
import de.samply.reporter.logger.Logger;
import de.samply.reporter.template.SheetTemplate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WorkbookManager {

    private final static Logger logger = BufferedLoggerFactory.getLogger(WorkbookManager.class);
    private final Integer workbookWindow;
    private final Integer maxNumberOfRows;
    private List<Workbook> workbooks = new ArrayList<>();
    private Map<SheetTemplate, List<Sheet>> templateSheetsMap = new HashMap<>();

    public WorkbookManager(Integer workbookWindow, Integer maxNumberOfRows) {
        this.workbookWindow = workbookWindow;
        this.maxNumberOfRows = maxNumberOfRows;
    }


    private Optional<Sheet> fetchLastSheet(SheetTemplate template) {
        List<Sheet> sheets = templateSheetsMap.get(template);
        return (sheets != null) ? Optional.of(sheets.get(sheets.size() - 1)) : Optional.empty();
    }

    public Sheet fetchLastSheetAndCreateIfNotExist (SheetTemplate template){
        Optional<Sheet> lastSheet = fetchLastSheet(template);
        return (lastSheet.isPresent()) ? lastSheet.get() : createSheet(template);
    }

    public void apply(SheetTemplate template, Consumer<Sheet> sheetConsumer) {
        List<Sheet> sheets = templateSheetsMap.get(template);
        if (sheets != null) {
            sheets.forEach(sheet -> sheetConsumer.accept(sheet));
        }
    }

    public void apply(Consumer<Workbook> workbookConsumer) {
        workbooks.forEach(workbookConsumer);
    }

    public Row createRow(SheetTemplate template) {
        Optional<Sheet> lastSheet = fetchLastSheet(template);
        Sheet sheet = (lastSheet.isPresent()) ? lastSheet.get() : createSheet(template);
        int index = sheet.getLastRowNum() + 1;
        if (index > maxNumberOfRows) {
            sheet = createSheet(template);
            index = 0;
        }
        if (index == 0) {
            createHeaderRow(sheet, template);
            index = 1;
        }
        return sheet.createRow(index);
    }

    public Sheet createSheet(SheetTemplate sheetTemplate) {
        Optional<Sheet> lastSheet = fetchLastSheet(sheetTemplate);
        Workbook workbook = (lastSheet.isEmpty()) ? fetchFirstWorkbookOrCreateIfNotExist() :
                fetchNextWorkbookOrCreateIfNotExist(lastSheet.get().getWorkbook());
        return createSheet(workbook, sheetTemplate);
    }

    private Optional<Workbook> fetchFirstWorkbook() {
        return (workbooks.size() > 0) ? Optional.of(workbooks.get(0)) : Optional.empty();
    }

    private Optional<Workbook> fetchNextWorkbook(Workbook workbook) {
        int index = workbooks.indexOf(workbook);
        return (index + 1 < workbooks.size()) ? Optional.of(workbooks.get(index + 1)) : Optional.empty();
    }

    private Workbook fetchNextWorkbookOrCreateIfNotExist(Workbook workbook) {
        Optional<Workbook> nextWorkbook = fetchNextWorkbook(workbook);
        return (nextWorkbook.isPresent()) ? nextWorkbook.get() : createWorkbook();
    }

    private Workbook fetchFirstWorkbookOrCreateIfNotExist() {
        Optional<Workbook> firstWorkbook = fetchFirstWorkbook();
        return (firstWorkbook.isPresent()) ? firstWorkbook.get() : createWorkbook();
    }

    private Workbook createWorkbook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(workbookWindow);
        workbooks.add(workbook);
        return workbook;
    }

    private Sheet createSheet(Workbook workbook, SheetTemplate template) {
        Sheet sheet = workbook.createSheet(template.getName());
        if (sheet instanceof SXSSFSheet) {
            ((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
        }
        List<Sheet> sheets = templateSheetsMap.get(template);
        if (sheets == null) {
            sheets = new ArrayList<>();
            templateSheetsMap.put(template, sheets);
        }
        sheets.add(sheet);
        return sheet;
    }

    private void createHeaderRow(Sheet sheet, SheetTemplate template) {
        if (!template.getColumnTemplates().isEmpty()) {
            logger.info("Creating header row for sheet '" + template.getName() + "'...");
            Row row = sheet.createRow(0);
            AtomicInteger counter = new AtomicInteger(0);
            template.getColumnTemplates().forEach(
                    columnTemplate -> row.createCell(counter.getAndIncrement())
                            .setCellValue(columnTemplate.getName()));
            sheet.createFreezePane(0, 1);
            boldHeaderRow(sheet.getWorkbook(), row);
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

    public void writeWorkbook(Path path) {
        apply(workbook -> writeWorkbook(path, workbook));
    }

    private void writeWorkbook(Path path, Workbook workbook) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fetchRealPath(path, workbook).toFile())) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path fetchRealPath(Path path, Workbook workbook) {
        return (workbooks.size() > 1 && workbooks.indexOf(workbook) > 0) ?
                path.getParent().resolve(createFilenameWithCounter(path, workbooks.indexOf(workbook) + 1)) : path;
    }

    public List<Path> fetchRealPaths(Path path) {
        return workbooks.stream().map(workbook -> fetchRealPath(path, workbook)).toList();
    }

    private String createFilenameWithCounter(Path path, int counter) {
        String filename = path.getFileName().toString();
        int index = filename.lastIndexOf(".");
        if (counter > 1) {
            if (index > 0) {
                String extension = filename.substring(index);
                filename = filename.substring(0, index);
                filename = filename + '_' + counter + extension;
            } else {
                filename += counter;
            }
        }
        return filename;
    }

    public boolean isMultiWorkbook() {
        return workbooks.size() > 1;
    }
}
