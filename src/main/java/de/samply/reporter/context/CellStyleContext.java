package de.samply.reporter.context;

import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class CellStyleContext {

  private Set<Cell> formattedCells = new HashSet<>();
  private Workbook workbook;

  public CellStyleContext(Workbook workbook) {
    this.workbook = workbook;
  }

  public void addCellStyleToCell(Cell cell, CellStyle cellStyle) {
    if (formattedCells.contains(cell)) {
      CellStyle newCellStyle = createCellStyle();
      newCellStyle.cloneStyleFrom(cell.getCellStyle());
      newCellStyle.cloneStyleFrom(cellStyle);
      cell.setCellStyle(newCellStyle);
    } else {
      cell.setCellStyle(cellStyle);
      formattedCells.add(cell);
    }
  }

  public CellStyle createCellStyle() {
    return workbook.createCellStyle();
  }

  public Workbook getWorkbook() {
    return workbook;
  }

}
