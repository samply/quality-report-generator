<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<%
    CellContext celldataModel = cellContext
    Context dataModel = context
%>
<%
    celldataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        cellStyle.setFont(font)
    }
%>
<%
    def attributeIndex = dataModel.getColumnIndex("filtered elements", "validation")
    celldataModel.setCondition { cell -> cell.getRow().getCell(2).getStringCellValue().equalsIgnoreCase("mismatch") }
%>
