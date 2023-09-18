package de.samply.reporter.utils.poi;

import org.apache.poi.ss.usermodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class SheetSorter {

    private final Map<Integer, SortInfo> columnOrderMap = new HashMap<>();
    private final static DataFormatter dataFormatter = new DataFormatter();
    private static BiFunction<Row, Integer, String> defaultRowColumnExtractor = (row, column) ->
            dataFormatter.formatCellValue(row.getCell(column));


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
            copyValue(cell, newCell);
            CellStyle cellStyle = cell.getCellStyle();
            newCell.setCellStyle(cellStyle);
        });
    }

    private void copyValue(Cell sourceCell, Cell targetCell) {
        switch (sourceCell.getCellType()) {
            case BLANK:
                targetCell.setBlank();
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
        }
    }

}
