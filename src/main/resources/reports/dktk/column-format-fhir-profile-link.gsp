<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Hyperlink; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<% Context dataModel = context %>
<%
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
    cellDataModel.addCellModifier { cell ->
        def metaInfo = attributeMetaInfo[cell.getStringCellValue()]
        if (metaInfo != null){
            def url = metaInfo[2]
            if (url != null && url.trim().size() > 0){
                Hyperlink hyperlink = cell.getSheet().getWorkbook().getCreationHelper().createHyperlink(org.apache.poi.common.usermodel.HyperlinkType.URL)
                hyperlink.setAddress(url)
                cell.setHyperlink(hyperlink)
            }
        }
    }
%>
