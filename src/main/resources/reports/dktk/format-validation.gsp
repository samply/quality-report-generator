<%@ page import="org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<% CellContext dataModel = cellContext %>
<%
    dataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        cellStyle.setFont(font)
    }
%>
<%
    dataModel.setCondition { cell -> cell.getRow().getCell(2).getStringCellValue().equalsIgnoreCase("mismatch") }
%>
