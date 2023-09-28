<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Cell; org.apache.poi.ss.usermodel.Hyperlink; org.apache.poi.ss.usermodel.Row; org.apache.poi.common.usermodel.HyperlinkType; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<%
    CellContext cellDataModel = cellContext
    Context dataModel = context
%>
<%
    cellDataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        font.setBold(true)
        cellStyle.setFont(font)
    }
%>
<%
    cellDataModel.addCellModifier { cell ->
        def hyperlink = cell.getSheet().getWorkbook().getCreationHelper().createHyperlink(org.apache.poi.common.usermodel.HyperlinkType.DOCUMENT)
        def sheet = cell.getSheet().getWorkbook().getSheet("patient local ids")
        if (sheet != null) {
            def patientIdsRow = sheet.getRow(0)
            for (tempCell in patientIdsRow) {
                def attributeIndex = dataModel.getColumnIndex("filtered elements", "data element FHIR")
                def valueIndex = dataModel.getColumnIndex("filtered elements", "value FHIR")
                if (cellDataModel.getCellValueAsString(tempCell).contains(cellDataModel.getCellValueAsString(cell.getRow().getCell(attributeIndex))) && cellDataModel.getCellValueAsString(tempCell).contains(cellDataModel.getCellValueAsString(cellDataModel.getCellValueAsString(cell.getRow().getCell(valueIndex))))) {
                    char columnLetter = (char) (((int) 'A') + tempCell.columnIndex)
                    def address = "'" + patientIdsRow.getSheet().getSheetName() + "'!" + columnLetter + "1"
                    hyperlink.setAddress(address)
                    cell.setHyperlink(hyperlink)
                    break
                }
            }
        }
    }
%>
<% cellDataModel.setCondition { cell -> cellDataModel.getCellValueAsString(cell).equalsIgnoreCase("mismatch") } %>
