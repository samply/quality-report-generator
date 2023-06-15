<%@ page import="org.apache.poi.ss.usermodel.Cell; org.apache.poi.ss.usermodel.Hyperlink; org.apache.poi.ss.usermodel.Row; org.apache.poi.common.usermodel.HyperlinkType; org.apache.poi.ss.usermodel.Font; de.samply.reporter.context.CellContext" %>
<% CellContext dataModel = cellContext %>
<%
    dataModel.setCellStyle { workbook, cellStyle ->
        def font = workbook.createFont()
        font.setColor(org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.RED.getIndex())
        font.setBold(true)
        cellStyle.setFont(font)
    }
%>
<%
    dataModel.addCellModifier { cell ->
        def hyperlink = cell.getSheet().getWorkbook().getCreationHelper().createHyperlink(org.apache.poi.common.usermodel.HyperlinkType.DOCUMENT)
        def patientIdsRow = cell.getSheet().getWorkbook().getSheet("patient local ids").getRow(0)
        for (tempCell in patientIdsRow) {
            if (tempCell.getStringCellValue().contains(cell.getRow().getCell(0).getStringCellValue()) && tempCell.getStringCellValue().contains(cell.getRow().getCell(1).getStringCellValue())) {
                char columnLetter = (char) (((int) 'A') + tempCell.columnIndex)
                def address = "'" + patientIdsRow.getSheet().getSheetName() + "'!" + columnLetter + "1"
                hyperlink.setAddress(address)
                cell.setHyperlink(hyperlink)
                break
            }
        }
    }
%>
<% dataModel.setCondition { cell -> cell.getStringCellValue().equalsIgnoreCase("mismatch") } %>
