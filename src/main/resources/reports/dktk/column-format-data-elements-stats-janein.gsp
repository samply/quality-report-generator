<%@ page import="org.apache.poi.ss.usermodel.FillPatternType; org.apache.poi.ss.usermodel.IndexedColors; de.samply.reporter.context.CellContext" %>
<% CellContext dataModel = cellContext %>
<%
    dataModel.setCellStyle { workbook, cellStyle ->
        cellStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
    }
%>
<% dataModel.setCondition { cell -> cell.getStringCellValue().equalsIgnoreCase("ja") } %>
