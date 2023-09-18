<%@ page import="org.apache.poi.ss.usermodel.FillPatternType; org.apache.poi.ss.usermodel.IndexedColors; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        cellStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
    }
%>
<% cellDataModel.setCondition { cell -> cellDataModel.getCellValueAsString(cell).equalsIgnoreCase("ja") } %>
