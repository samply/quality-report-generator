<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<%
    CellContext cellDataModel = cellContext
    Context dataModel = context
%>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        cellStyle.setFont(font)
    }
%>
<%
    def attributeIndex = dataModel.getColumnIndex("filtered elements", "validation")
    cellDataModel.setCondition { cell -> cellDataModel.getCellValueAsString(cell.getRow().getCell(2)).equalsIgnoreCase("mismatch") }
%>
