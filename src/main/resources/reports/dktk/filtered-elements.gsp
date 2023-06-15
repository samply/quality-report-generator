<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"
    def DELIMITER = dataModel.getCsvConfig().delimiter()
    def match = "match"
    def mismatch = "mismatch"
    def emptyValue = ""

    def totalNumberOfPatients = dataModel.getElement("total number of patients")
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
%>
<%
    def printLine = { attribute, value, valueMatch, numberOfPatientsForAttribute, patientsWithAttributeValue ->
        def numberOfPatientsForAttributeValue = (patientsWithAttributeValue != null) ? patientsWithAttributeValue.size() : 0
        def lineElements = [
                attribute,
                value,
                (numberOfPatientsForAttributeValue > 0) ? valueMatch : emptyValue,
                numberOfPatientsForAttributeValue,
                (numberOfPatientsForAttribute != 0) ? (100.0 * numberOfPatientsForAttributeValue / numberOfPatientsForAttribute).round(1) : 0,
                (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttributeValue / totalNumberOfPatients).round(1) : 0
        ]
        if (lineElements[0] != null && lineElements[0].trim().size() > 0) {
%>
${lineElements.join(DELIMITER)}
<%
            }
    }
%>
<%
    dataModel.getKeySet(patientsProAttributeKey).forEach { attribute ->
        def patientsForAttribute = ((Set) dataModel.getElement(patientsProAttributeKey, attribute))
        def numberOfPatientsForAttribute = (patientsForAttribute != null) ? patientsForAttribute.size() : 0
        def isToBeFiltered = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][1] : false
        def filteredPatients = [] as Set
        def filteredValue = null
        def isEmptyValueToBeIgnored = dataModel.getKeySet(patientsProAttributeValueKey, attribute).size() > 1
        dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
            if (isToBeFiltered && dataModel.getElement(validationKey, attribute, value) == null) {
                filteredPatients.addAll(dataModel.getElement(patientsProAttributeValueKey, attribute, value))
                filteredValue = value
            }
        }
        if (filteredValue != null && (!filteredValue.equals(emptyValue) || !isEmptyValueToBeIgnored)) {
            if (filteredValue.trim().equals(emptyValue)){
                filteredPatients = [] as Set
            }
            printLine(attribute, filteredValue, match, numberOfPatientsForAttribute, filteredPatients)
        }
        dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
            if ((!isToBeFiltered || dataModel.getElement(validationKey, attribute, value) != null) && (!value.equals(emptyValue) || !isEmptyValueToBeIgnored)) {
                def patients = (value.trim().equals(emptyValue)) ? [] : dataModel.getElement(patientsProAttributeValueKey, attribute, value)
                def valueMatch = (dataModel.getElement(validationKey, attribute, value) == null) ? match : mismatch
                printLine(attribute, value, valueMatch, numberOfPatientsForAttribute, patients)
            }
        }
    }
%>
