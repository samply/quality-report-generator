<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def validationKey = "Validation"
    def DELIMITER = dataModel.getCsvConfig().delimiter()
    def HEADER_DELIMITER = '::'
    def patientIdDktkLokalIdMap = dataModel.getElement("Patient Id - DKTK-ID Map")
%>
<%
    def headerLine = [] as List
    def errorsLine = [] as List
    dataModel.getKeySet(validationKey).forEach { attribute ->
        dataModel.getKeySet(validationKey, attribute).forEach { value ->
            def error = dataModel.getElement(validationKey, attribute, value)
            if (error != null) {
                headerLine.add(attribute + HEADER_DELIMITER + value)
                errorsLine.add(error[0])
            }
        }
    }
%>
${headerLine.join(DELIMITER)}
${errorsLine.join(DELIMITER)}
<%
    def areMorePatientIds = (headerLine.size() > 0)
    def lineNumber = -1
    while (areMorePatientIds) {
        lineNumber++
        def lineElements = [] as List
        areMorePatientIds = false
        headerLine.forEach { attributeValue ->
            def split = attributeValue.split(HEADER_DELIMITER)
            def patientIds = dataModel.getElement(validationKey, split[0], split[1])[1]
            def patientId = (lineNumber < patientIds.size()) ? patientIds[lineNumber] : ''
            if (patientId != ''){
                def tempPatientId = patientIdDktkLokalIdMap[patientId]
                if (tempPatientId != null){
                    patientId = tempPatientId
                }
            }
            lineElements.add(patientId)
            if (lineNumber + 1 < patientIds.size()){
                areMorePatientIds = true
            }
        }
%>
${lineElements.join(DELIMITER)}
<% } %>
