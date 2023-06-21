<%@ page import="de.samply.reporter.utils.poi.SortOrder; de.samply.reporter.utils.SheetUtils; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<%
    cellDataModel.addSheetModifier {sheet ->
        de.samply.reporter.utils.SheetUtils.sort(sheet, de.samply.reporter.utils.poi.SortOrder.ASCENDING, 1)
    }
%>
