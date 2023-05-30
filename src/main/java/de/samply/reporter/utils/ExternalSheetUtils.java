package de.samply.reporter.utils;

import de.samply.reporter.template.SheetTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExternalSheetUtils {

  public static void addSheetFromSourceExcelFile(Workbook workbook, SheetTemplate template)
      throws ExternalSheetUtilsException {
    try (InputStream inputStream = fetchInputStream(template)) {
      if (inputStream != null) {
        addSheetFromSourceExcelFile(workbook, template, inputStream);
      }
    } catch (IOException e) {
      throw new ExternalSheetUtilsException(e);
    }
  }

  private static void addSheetFromSourceExcelFile(Workbook workbook, SheetTemplate template,
      InputStream inputStream)
      throws ExternalSheetUtilsException {
    try (Workbook sourceWorkbook = WorkbookFactory.create(inputStream)) {
      if (sourceWorkbook != null) {
        Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
        if (sourceSheet != null) {
          copy(sourceSheet, workbook.createSheet(template.getName()));
        }
      }
    } catch (IOException e) {
      throw new ExternalSheetUtilsException(e);
    }
  }

  private static InputStream fetchInputStream(SheetTemplate template)
      throws ExternalSheetUtilsException {
    try {
      return fetchInputStreamWihoutExceptionHandling(template);
    } catch (IOException e) {
      throw new ExternalSheetUtilsException(e);
    }
  }

  private static InputStream fetchInputStreamWihoutExceptionHandling(SheetTemplate template)
      throws IOException {
    InputStream result = null;
    if (template.getFileUrl() != null) {
      result = new URL(template.getFileUrl()).openStream();
    }
    if (template.getFilePath() != null) {
      result = Files.newInputStream(Path.of(template.getFilePath()));
    }
    return result;
  }

  private static void copy(Sheet sourceSheet, Sheet targetSheet) {
    int maxColumnNum = 0;
    for (int i = 0; i < sourceSheet.getLastRowNum() + 1; i++) {
      Row sourceRow = sourceSheet.getRow(i);
      Row newRow = targetSheet.createRow(i);

      if (sourceRow != null) {
        copyRow(sourceRow, newRow);

        if (sourceRow.getLastCellNum() > maxColumnNum) {
          maxColumnNum = sourceRow.getLastCellNum();
        }
      }
    }

    for (int i = 0; i < maxColumnNum; i++) {
      targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
    }
  }

  private static void copyRow(Row sourceRow, Row targetRow) {
    targetRow.setHeight(sourceRow.getHeight());

    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
      Cell sourceCell = sourceRow.getCell(j);
      Cell targetCell = targetRow.createCell(j);

      if (sourceCell != null) {
        targetCell.setCellStyle(sourceCell.getCellStyle());

        switch (sourceCell.getCellType()) {
          case BLANK:
            targetCell.setCellValue("");
            break;
          case BOOLEAN:
            targetCell.setCellValue(sourceCell.getBooleanCellValue());
            break;
          case ERROR:
            targetCell.setCellValue(sourceCell.getErrorCellValue());
            break;
          case FORMULA:
            targetCell.setCellFormula(sourceCell.getCellFormula());
            break;
          case NUMERIC:
            targetCell.setCellValue(sourceCell.getNumericCellValue());
            break;
          case STRING:
            targetCell.setCellValue(sourceCell.getRichStringCellValue());
            break;
        }
        copyMergedRegions(sourceCell, sourceRow, targetRow);
        copyCellStyles(sourceCell, targetCell);
      }
    }
  }

  private static void copyMergedRegions(Cell sourceCell, Row sourceRow, Row targetRow) {
    int numMergedRegions = sourceRow.getSheet().getNumMergedRegions();
    for (int i = 0; i < numMergedRegions; i++) {
      CellRangeAddress mergedRegion = sourceRow.getSheet().getMergedRegion(i);

      if (mergedRegion.isInRange(sourceRow.getRowNum(), sourceCell.getColumnIndex())) {
        CellRangeAddress newMergedRegion = getAdjustedMergedRegion(mergedRegion, targetRow);
        if (isMergedRegionValid(targetRow.getSheet(), newMergedRegion)) {
          targetRow.getSheet().addMergedRegion(newMergedRegion);
        }
      }
    }
  }

  private static void copyCellStyles(Cell sourceCell, Cell targetCell) {
    CellStyle sourceCellStyle = sourceCell.getCellStyle();
    CellStyle newCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
    newCellStyle.cloneStyleFrom(sourceCellStyle);
    targetCell.setCellStyle(newCellStyle);
  }

  private static CellRangeAddress getAdjustedMergedRegion(CellRangeAddress mergedRegion,
      Row newRow) {
    int newRowNum = newRow.getRowNum();
    int firstRow = mergedRegion.getFirstRow();
    int lastRow = mergedRegion.getLastRow();
    int firstCol = mergedRegion.getFirstColumn();
    int lastCol = mergedRegion.getLastColumn();

    return new CellRangeAddress(
        newRowNum + (firstRow - mergedRegion.getFirstRow()),
        newRowNum + (lastRow - mergedRegion.getFirstRow()),
        firstCol,
        lastCol
    );
  }

  private static boolean isMergedRegionValid(Sheet sheet, CellRangeAddress mergedRegion) {
    int numMergedRegions = sheet.getNumMergedRegions();
    for (int i = 0; i < numMergedRegions; i++) {
      CellRangeAddress existingRegion = sheet.getMergedRegion(i);
      if (existingRegion.getFirstRow() == mergedRegion.getFirstRow()
          && existingRegion.getLastRow() == mergedRegion.getLastRow()
          && existingRegion.getFirstColumn() == mergedRegion.getFirstColumn()
          && existingRegion.getLastColumn() == mergedRegion.getLastColumn()) {
        return false; // Merged region already exists
      }
    }
    return true;
  }

}
