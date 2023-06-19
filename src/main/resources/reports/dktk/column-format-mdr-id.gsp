<%@ page import="de.samply.reporter.context.Context; org.apache.poi.ss.usermodel.Hyperlink; de.samply.reporter.context.CellContext" %>
<% CellContext cellDataModel = cellContext %>
<% Context dataModel = context %>
<%
    def createMdrLink = urn -> 'https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=' + ((String) urn).replace(":", "%3A")
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
    cellDataModel.addCellModifier { cell ->
        def index = dataModel.getColumnIndex("filtered elements", "FHIR attribute")
        def metaInfo = attributeMetaInfo[cell.getRow().getCell(index).getStringCellValue()]
        if (metaInfo != null){
            def url = metaInfo[4]
            if (url != null && url.trim().size() > 0){
                def hyperlink = cell.getSheet().getWorkbook().getCreationHelper().createHyperlink(org.apache.poi.common.usermodel.HyperlinkType.URL)
                hyperlink.setAddress(createMdrLink(url))
                cell.setHyperlink(hyperlink)
                cell.setCellValue(url.replace("dataelement:", ""))
            }
        }
    }
%>
