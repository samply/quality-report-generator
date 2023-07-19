<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"
    def existsValidationKey = "Exists Validation for Attribute"
    def DELIMITER = dataModel.getCsvConfig().delimiter()
    def match = "match"
    def mismatch = "mismatch"
    def notFound = "not found"
    def notValidated = "not validated"
    def emptyValue = ""
    def filteredDataTypes = ['Integer', 'Float', 'String', 'Date', 'Datetime', 'Time']

    def totalNumberOfPatients = dataModel.getElement("total number of patients")
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
%>
<%
    def printLine = { mdrId, dktkId, dataType, attribute, value, valueMatch, numberOfPatientsForAttribute, patientsWithAttributeValue ->
        def numberOfPatientsForAttributeValue = (patientsWithAttributeValue != null) ? patientsWithAttributeValue.size() : 0
        def lineElements = [
                mdrId,
                dktkId,
                attribute,
                value,
                dataType,
                (numberOfPatientsForAttributeValue > 0) ? valueMatch : notFound,
                numberOfPatientsForAttributeValue,
                (numberOfPatientsForAttribute != 0) ? (100.0 * numberOfPatientsForAttributeValue / numberOfPatientsForAttribute).round(1) : 0,
                (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttributeValue / totalNumberOfPatients).round(1) : 0
        ]
        if (lineElements[2] != null && lineElements[2].trim().size() > 0) {
%>
${lineElements.join(DELIMITER)}
<%
            }
    }
%>
<%
    dataModel.getKeySet(patientsProAttributeKey).findAll { attribute -> !attribute.toLowerCase().endsWith('-validation') && !attribute.toLowerCase().endsWith('-id') }.forEach { attribute ->
        def patientsForAttribute = ((Set) dataModel.getElement(patientsProAttributeKey, attribute))
        def numberOfPatientsForAttribute = (patientsForAttribute != null) ? patientsForAttribute.size() : 0
        def isToBeFiltered = (attributeMetaInfo[attribute] != null && attributeMetaInfo[attribute][6] != null && filteredDataTypes.contains(attributeMetaInfo[attribute][6]))
        def mdrId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][4] : emptyValue
        def dktkId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][7] : emptyValue
        def dataType = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][6] : emptyValue
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
            if (filteredValue.trim().equals(emptyValue)) {
                filteredPatients = [] as Set
            }
            printLine(mdrId, dktkId, dataType, attribute, filteredValue, match, numberOfPatientsForAttribute, filteredPatients)
        }
        dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
            if ((!isToBeFiltered || dataModel.getElement(validationKey, attribute, value) != null) && (!value.equals(emptyValue) || !isEmptyValueToBeIgnored)) {
                def patients = (value.trim().equals(emptyValue)) ? [] : dataModel.getElement(patientsProAttributeValueKey, attribute, value)
                def valueMatch = (dataModel.getElement(validationKey, attribute, value) == null) ?
                        ((dataModel.getElement(existsValidationKey).contains(attribute)) ? match : notValidated) : mismatch
                printLine(mdrId, dktkId, dataType, attribute, value, valueMatch, numberOfPatientsForAttribute, patients)
            }
        }
    }
%>
