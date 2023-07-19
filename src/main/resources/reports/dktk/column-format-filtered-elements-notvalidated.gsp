<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Cell; org.apache.poi.ss.usermodel.Hyperlink; org.apache.poi.ss.usermodel.Row; org.apache.poi.common.usermodel.HyperlinkType; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<%
    CellContext cellDataModel = cellContext
    Context dataModel = context
%>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GOLD.getIndex())
        font.setBold(true)
        cellStyle.setFont(font)
    }
%>
<% cellDataModel.setCondition { cell -> cell.getStringCellValue().equalsIgnoreCase("not validated") } %>
