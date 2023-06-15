<%@ page import="de.samply.reporter.context.CellContext" %>
<% CellContext dataModel = cellContext %>
<%
    dataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        cellStyle.setFont(font)
    }
%>
<%
    dataModel.setCondition { cell -> cell.getRowIndex() == 1 }
%>
