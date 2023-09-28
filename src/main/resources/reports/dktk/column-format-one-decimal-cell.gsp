<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Cell; org.apache.poi.ss.usermodel.Hyperlink; org.apache.poi.ss.usermodel.Row; org.apache.poi.common.usermodel.HyperlinkType; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        def dataFormat = workbook.createDataFormat()
        def numberFormatIndex = dataFormat.getFormat("0.0")
        cellStyle.setDataFormat(numberFormatIndex)
    }
%>
