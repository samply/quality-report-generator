package de.samply.reporter.context;

import de.samply.reporter.utils.multilevel.MultiLevelComparable;
import de.samply.reporter.utils.multilevel.MultilevelComparableFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CellContext {

  private final CellStyleContext cellStyleContext;
  private CellStyle cellStyle;
  private Function<Cell, Boolean> condition;
  private final List<Consumer<Cell>> cellModifiers = new ArrayList<>();
  private final List<Consumer<Sheet>> sheetModifiers = new ArrayList<>();

  public CellContext(CellStyleContext cellStyleContext) {
    this.cellStyleContext = cellStyleContext;
  }

  public void setCondition(
      Function<Cell, Boolean> condition) {
    this.condition = condition;
  }

  public void setCellStyle(BiConsumer<Workbook, CellStyle> cellStyleConsumer) {
    cellStyle = cellStyleContext.createCellStyle();
    cellStyleConsumer.accept(cellStyleContext.getWorkbook(), cellStyle);
  }

  public void addCellModifier(Consumer<Cell> cellModifier) {
    cellModifiers.add(cellModifier);
  }

  public void addSheetModifier(Consumer<Sheet> sheetModifier) {
    sheetModifiers.add(sheetModifier);
  }

  public void applyCellStyleToCell(Cell cell) {
    if (cell != null) {
      if (condition == null || (condition.apply(cell))) {
        if (cellStyle != null) {
          cellStyleContext.addCellStyleToCell(cell, cellStyle);
        }
        cellModifiers.forEach(cellModifier -> cellModifier.accept(cell));
      }
    }
  }

  public void applySheetStyleToSheet(Sheet sheet){
    if (sheet != null){
      sheetModifiers.forEach(sheetModifier -> sheetModifier.accept(sheet));
    }
  }

}
