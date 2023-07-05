<%@ page import="de.samply.reporter.utils.multilevel.MultilevelComparableFactory; org.apache.poi.ss.usermodel.Row; de.samply.reporter.utils.poi.SortOrder; de.samply.reporter.utils.SheetUtils; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<%
    def columnNumber = 1
    def rowColumnExtractor = (row, column) -> {
        def indexes = []
        def cellValue = row.getCell(columnNumber).getStringCellValue()
        if (cellValue != null && cellValue.trim().size() > 0) {
            indexes.addAll(cellValue.split('-'))
        } else {
            indexes.addAll('Z-1'.split('-'))
        }
        cellValue = row.getCell(0).getStringCellValue()
        if (cellValue != null && cellValue.trim().size() > 0) {
            indexes.addAll(cellValue.split(':'))
        } else{
            indexes.addAll('urn:zzzz:1:1'.split(':'))
        }
        cellValue = row.getCell(2).getStringCellValue()
        if (cellValue != null && cellValue.trim().size() > 0) {
            indexes.add(cellValue)
        }
        return de.samply.reporter.utils.multilevel.MultilevelComparableFactory.create(indexes.toArray(new String[indexes.size()]))
    }
    cellDataModel.addSheetModifier { sheet ->
        de.samply.reporter.utils.SheetUtils.sort(sheet, de.samply.reporter.utils.poi.SortOrder.ASCENDING, columnNumber, rowColumnExtractor)
    }
%>
