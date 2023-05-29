package de.samply.reporter.context;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class CellContext {

  private Workbook workbook;
  private CellStyle cellStyle;
  private Function<Cell, Boolean> condition;

  public CellContext(Workbook workbook) {
    this.workbook = workbook;
    this.cellStyle = workbook.createCellStyle();
  }

  public void setCondition(
      Function<Cell, Boolean> condition) {
    this.condition = condition;
  }

  public void setCellStyle(BiConsumer<Workbook, CellStyle> cellStyleConsumer) {
    cellStyleConsumer.accept(workbook, cellStyle);
  }

  public void applyCellStyleToCell(Cell cell) {
    if (cell != null && cellStyle != null) {
      if (condition == null || (condition != null && condition.apply(cell))) {
        cell.setCellStyle(cellStyle);
      }
    }
  }

}
