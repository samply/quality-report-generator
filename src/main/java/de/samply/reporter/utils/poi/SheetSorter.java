package de.samply.reporter.utils.poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetSorter {

  private Map<Integer, SortOrder> columnOrderMap = new HashMap<>();

  public void addSortKey(int column, SortOrder order) {
    columnOrderMap.put(column, order);
  }

  public void sortSheet(Sheet sheet) {
    columnOrderMap.keySet().forEach(column -> sortSheet(sheet, column, columnOrderMap.get(column)));
  }

  private void sortSheet(Sheet sheet, int column, SortOrder order) {
    List<Row> rows = new ArrayList<>();
    sheet.forEach(row -> rows.add(row));
    Comparator<Row> comparator = Comparator.comparing(
        row -> row.getCell(column).getStringCellValue());
    Collections.sort(rows, (order == SortOrder.ASCENDING) ? comparator : comparator.reversed());
    rows.forEach(row -> cloneRowInSheet(sheet, row));
    rows.forEach(sheet::removeRow);
  }

  private void cloneRowInSheet(Sheet sheet, Row row) {
    Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
    row.forEach(cell -> {
      Cell newCell = newRow.createCell(row.getRowNum());
      newCell.setCellValue(cell.getStringCellValue());
      CellStyle cellStyle = cell.getCellStyle();
      newCell.setCellStyle(cellStyle);
    });
  }

}