package de.samply.reporter.utils.poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetSorter {

  private Map<Integer, SortInfo> columnOrderMap = new HashMap<>();
  private static BiFunction<Row, Integer, String> defaultRowColumnExtractor = (row, column) -> row.getCell(
      column).getStringCellValue();

  private static class SortInfo<C extends Comparable> {

    private SortOrder order;
    private BiFunction<Row, Integer, C> rowColumnExtractor;

    public SortInfo(SortOrder order, BiFunction<Row, Integer, C> rowColumnExtractor) {
      this.order = order;
      this.rowColumnExtractor = rowColumnExtractor;
    }

    public SortOrder getOrder() {
      return order;
    }

    public BiFunction<Row, Integer, C> getRowColumnExtractor() {
      return rowColumnExtractor;
    }

  }

  private static class StringSortInfo extends SortInfo<String> {

    private StringSortInfo(SortOrder order) {
      super(order, defaultRowColumnExtractor);
    }
  }

  public void addSortKey(int column, SortOrder order) {
    columnOrderMap.put(column, new StringSortInfo(order));
  }

  public <C extends Comparable> void addSortKey(int column, SortOrder order,
      BiFunction<Row, Integer, C> rowColumnExtractor) {
    columnOrderMap.put(column, new SortInfo(order, rowColumnExtractor));
  }


  public void sortSheet(Sheet sheet) {
    columnOrderMap.keySet().forEach(column -> sortSheet(sheet, column, columnOrderMap.get(column)));
  }

  private void sortSheet(Sheet sheet, int column, SortInfo sortInfo) {
    List<Row> rows = new ArrayList<>();
    sheet.forEach(row -> {
      if (row.getRowNum() != 0) {
        rows.add(row);
      }
    });
    Comparator<Row> comparator = Comparator.comparing(
        row -> (Comparable) sortInfo.getRowColumnExtractor().apply(row, column));
    Collections.sort(rows,
        (sortInfo.getOrder() == SortOrder.ASCENDING) ? comparator : comparator.reversed());
    AtomicInteger counter = new AtomicInteger(1);
    rows.forEach(row -> cloneRowInSheet(sheet, row, counter));
    rows.forEach(sheet::removeRow);
  }

  private void cloneRowInSheet(Sheet sheet, Row row, AtomicInteger counter) {
    Row newRow = sheet.createRow(counter.getAndIncrement());
    row.forEach(cell -> {
      Cell newCell = newRow.createCell(cell.getColumnIndex());
      newCell.setCellValue(cell.getStringCellValue());
      CellStyle cellStyle = cell.getCellStyle();
      newCell.setCellStyle(cellStyle);
    });
  }

}
