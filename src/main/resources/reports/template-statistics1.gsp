<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"

    def DELIMITER = dataModel.getCsvConfig().delimiter()
%>
<%
    dataModel.getSourcePaths().forEach(path -> dataModel.applyToRecords(path, csvRecord -> {
        def patientId = csvRecord.get("Patient-ID")
        csvRecord.getParser().getHeaderMap().keySet().forEach(header -> {
            def value = csvRecord.get(header)

            if (!header.toLowerCase().contains("validation")) {

                def patients = dataModel.getElement(patientsProAttributeValueKey, header, value)
                if (patients == null) {
                    patients = [] as Set
                }
                ((Set) patients).add(patientId)
                dataModel.putElement(patients, patientsProAttributeValueKey, header, value)

                patients = dataModel.getElement(patientsProAttributeKey, header)
                if (patients == null) {
                    patients = [] as Set
                }
                ((Set) patients).add(patientId)
                dataModel.putElement(patients, patientsProAttributeKey, header)

            } else {

                if (value != null && value.trim().length() > 0) {
                    def valueHeader = null
                    for (String tempHeader : csvRecord.getParser().getHeaderMap().keySet()) {
                        if (header.contains(tempHeader) && header.length() > tempHeader.length()) {
                            valueHeader = tempHeader
                            break
                        }
                    }
                    if (valueHeader != null) {
                        def attributeValue = csvRecord.get(valueHeader)
                        def error = dataModel.getElement(validationKey, valueHeader, attributeValue)
                        if (error == null) {
                            def patientIdSet = [] as Set
                            error = [value, patientIdSet]
                            dataModel.putElement(error, validationKey, valueHeader, attributeValue)
                        }
                        ((Set) error[1]).add(patientId)
                    }
                }

            }

        })
    }))
%>
<%
    def patientIds = [] as Set
    dataModel.getAllElement(patientsProAttributeKey).forEach { tempPatientIds -> patientIds.addAll(tempPatientIds) }
    def totalNumberOfPatientsKey = "total number of patients"
    def totalNumberOfPatients = Integer.valueOf(patientIds.size())
    dataModel.putElement(totalNumberOfPatients, totalNumberOfPatientsKey)
%>
<% dataModel.getKeySet(patientsProAttributeKey).forEach { attribute ->
    def numberOfPatientsForAttribute = ((Set) dataModel.getElement(patientsProAttributeKey, attribute)).size()
    dataModel.getKeySet(patientsProAttributeValueKey, attribute).forEach { value ->
        def numberOfPatientsForAttributeValue = ((Set) dataModel.getElement(patientsProAttributeValueKey, attribute, value)).size()
        def lineElements = [
                attribute,
                value,
                (dataModel.getElement(validationKey, attribute, value) == null) ? "match" : "mismatch",
                numberOfPatientsForAttributeValue,
                100.0 * numberOfPatientsForAttributeValue / numberOfPatientsForAttribute,
                100.0 * numberOfPatientsForAttributeValue / totalNumberOfPatients
        ]
        if (lineElements[0].trim().size() > 0 && lineElements[1].trim().size() > 0) {
%>
${lineElements.join(DELIMITER)}
<% }}} %>
