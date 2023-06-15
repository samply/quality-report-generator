<%@ page import="de.samply.reporter.context.CellContext" %>
<% CellContext dataModel = cellContext %>
<%
    dataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setBold(true)
        cellStyle.setFont(font)
    }
%>
<%
    dataModel.setCondition { cell -> cell.getRowIndex() == 0 }
%>
