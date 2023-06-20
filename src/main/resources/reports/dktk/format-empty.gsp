<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<%
    CellContext cellDataModel = cellContext
    Context dataModel = context
%>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.ROYAL_BLUE.getIndex())
        cellStyle.setFont(font)
    }
%>
<%
    def index = dataModel.getColumnIndex("filtered elements", "FHIR value")
    cellDataModel.setCondition { cell -> cell.getRow().getCell(index).getStringCellValue().trim().equals("") }
%>
