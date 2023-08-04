<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"
    def existsValidationKey = "Exists Validation for Attribute"
    def match = "match"
    def mismatch = "mismatch"
    def notFound = "not found"
    def notValidated = "not validated"
    def emptyValue = ""
    def DELIMITER = dataModel.getCsvConfig().delimiter()
    def totalNumberOfPatients = dataModel.getElement("total number of patients")
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
%>
<% dataModel.getKeySet(patientsProAttributeKey).findAll { attribute -> !attribute.toLowerCase().endsWith('-validation') && !attribute.toLowerCase().endsWith('-id') }.forEach { attribute ->
    def patientsForAttribute = ((Set) dataModel.getElement(patientsProAttributeKey, attribute))
    def numberOfPatientsForAttribute = (patientsForAttribute != null) ? patientsForAttribute.size() : 0
    def isEmptyValueToBeIgnored = dataModel.getKeySet(patientsProAttributeValueKey, attribute).size() > 1
    def mdrId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][4] : emptyValue
    def dktkId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][7] : emptyValue
    def dataType = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][6] : emptyValue

    dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
        if (!value.equals(emptyValue) || !isEmptyValueToBeIgnored) {
            def patientsForAttributeValue = (value.trim().equals(emptyValue)) ? [] : ((Set) dataModel.getElement(patientsProAttributeValueKey, attribute, value))
            def numberOfPatientsForAttributeValue = (patientsForAttributeValue != null) ? patientsForAttributeValue.size() : 0
            def matchValue = (dataModel.getElement(validationKey, attribute, value) == null) ?
                    ((dataModel.getElement(existsValidationKey).contains(attribute)) ? match : notValidated) : mismatch
            def lineElements = [
                    mdrId,
                    dktkId,
                    attribute,
                    value,
                    dataType,
                    (numberOfPatientsForAttributeValue > 0) ? matchValue : notFound,
                    numberOfPatientsForAttributeValue,
                    (numberOfPatientsForAttribute != 0) ? (100.0 * numberOfPatientsForAttributeValue / numberOfPatientsForAttribute).round(1) : 0,
                    (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttributeValue / totalNumberOfPatients).round(1) : 0
            ]
            if (lineElements[2] != null && lineElements[2].trim().size() > 0) {
%>
${lineElements.join(DELIMITER)}
<% }
}
}
} %>
