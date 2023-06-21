<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"
    def DELIMITER = dataModel.getCsvConfig().delimiter()
    def yes = "ja"
    def no = "nein"
    def emptyValue = ""

    def totalNumberOfPatients = dataModel.getElement("total number of patients")
    def attributeMetaInfo = dataModel.getElement("Attribute Meta Info")
%>
<%
    dataModel.getKeySet(patientsProAttributeKey).findAll(attribute -> !attribute.equalsIgnoreCase("validation")).forEach { attribute ->
        def patientsForAttribute = ((Set) dataModel.getElement(patientsProAttributeKey, attribute))
        def numberOfPatientsForAttribute = (patientsForAttribute != null) ? patientsForAttribute.size() : 0
        def mdrId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][4] : emptyValue
        def dktkId = (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][7] : emptyValue
        def patientsWithMatch = [] as Set
        def patientsWithMismatch = [] as Set
        dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
            def patients = dataModel.getElement(patientsProAttributeValueKey, attribute, value)
            if (dataModel.getElement(validationKey, attribute, value) == null) {
                ((Set) patientsWithMatch).addAll(patients)
            } else {
                ((Set) patientsWithMismatch).addAll(patients)
            }
        }
        def numberOfPatientsForAttributeWithMatch = (patientsWithMatch != null) ? patientsWithMatch.size() : 0
        def numberOfPatientsForAttributeWithMismatch = (patientsWithMismatch != null) ? patientsWithMismatch.size() : 0
        def lineElements = [
                mdrId,
                dktkId,
                attribute,
                numberOfPatientsForAttribute,
                (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttribute / totalNumberOfPatients).round(1) : 0,
                numberOfPatientsForAttributeWithMatch,
                (numberOfPatientsForAttribute != 0) ? (100.0 * numberOfPatientsForAttributeWithMatch / numberOfPatientsForAttribute).round(1) : 0,
                (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttributeWithMatch / totalNumberOfPatients).round(1) : 0,
                numberOfPatientsForAttributeWithMismatch,
                (numberOfPatientsForAttribute != 0) ? (100.0 * numberOfPatientsForAttributeWithMismatch / numberOfPatientsForAttribute).round(1) : 0,
                (totalNumberOfPatients != 0) ? (100.0 * numberOfPatientsForAttributeWithMismatch / totalNumberOfPatients).round(1) : 0,
                (attributeMetaInfo[attribute] != null) ? attributeMetaInfo[attribute][0] : '',
                (numberOfPatientsForAttribute > 0) ? yes : no,
                (numberOfPatientsForAttribute != 0 && 100.0 * numberOfPatientsForAttributeWithMismatch / numberOfPatientsForAttribute < 10) ? yes : no,
                (numberOfPatientsForAttribute > 0 && 100.0 * numberOfPatientsForAttributeWithMismatch / numberOfPatientsForAttribute < 10) ? yes : no
        ]
        if (lineElements[0] != null && lineElements[0].trim().size() > 0) {
%>
${lineElements.join(DELIMITER)}
<% }
} %>
