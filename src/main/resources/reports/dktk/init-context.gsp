<%@ page import="de.samply.reporter.context.Context" %>
<% Context dataModel = context %>
<%
    def patientsProAttributeValueKey = "Patients pro attribute-value"
    def patientsProAttributeKey = "Patients pro attribute"
    def validationKey = "Validation"
    def patientIdDktkLokalIdMapKey = "Patient Id - DKTK-ID Map"
    def attributeMetaInfoKey = "Attribute Meta Info"
    def totalNumberOfPatientsKey = "total number of patients"
    def emptyValue = ""
%>
<!-- Attribute Meta Info - Elements:
1. MUSS / Soll / kann
2. To be filtered in sheet 'filtered elements' (boolean)
3. FHIR Profile Description URL
4. MDR Attribute
5. MDR-URN
-->
<%
    def attributeMetaInfo =
            ['Systemische Therapie Beginn'                         : ['kann', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717356'],
             'Primärdiagnose'                                      : ['MUSS', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717341'],
             'Morphologie-Freitext'                                : ['MUSS', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717316'],
             'Seitenlokalisation nach ADT-GEKID'                   : ['Soll', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717346'],
             'Geburtsdatum'                                        : ['Soll', true, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717337'],
             'Datum lokales oder regionäres Rezidiv'               : ['Soll', true, ''],
             'Gesamtbeurteilung Tumorstatus'                       : ['Soll', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717298'],
             'Morphologie'                                         : ['Soll', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717316'],
             'Fernmetastasen'                                      : ['MUSS', false, 'https://simplifier.net/packages/de.dktk.oncology/1.2.0/files/717286'],
             'System-Therapy-ID'                                   : ['MUSS', true, ''],
             'Metastasis-ID'                                       : ['MUSS', true, ''],
             'Untersuchungs-, Befunddatum im Verlauf'              : ['MUSS', true, ''],
             'Fernmetastasen vorhanden'                            : ['kann', false, ''],
             'Lokale Beurteilung Resttumor'                        : ['MUSS', false, ''],
             'Immuntherapie'                                       : ['kann', false, ''],
             'Systemische Therapie Stellung zu operativer Therapie': ['MUSS', false, ''],
             'c/p/u-Präfix N'                                      : ['MUSS', false, ''],
             'Version des ICD-10-Katalogs'                         : ['kann', true, ''],
             'Systemische Therapie Ende'                           : ['MUSS', false, ''],
             'Histologie Datum'                                    : ['MUSS', true, ''],
             'Lymphknoten-Rezidiv'                                 : ['MUSS', false, ''],
             'UICC Stadium'                                        : ['kann', false, ''],
             'c/p/u-Präfix M'                                      : ['MUSS', false, ''],
             'DKTK-ID-Lokal'                                       : ['MUSS', true, ''],
             'c/p/u-Präfix T'                                      : ['MUSS', false, ''],
             'Chemotherapie'                                       : ['MUSS', false, ''],
             'Tumor Diagnosedatum'                                 : ['MUSS', true, ''],
             'Lokales oder regionäres Rezidiv'                     : ['kann', false, ''],
             'Datum der OP'                                        : ['MUSS', true, ''],
             'Primaertumor Diagnosetext'                           : ['kann', false, ''],
             'TNM-r-Symbol'                                        : ['Soll', false, ''],
             'TNM-Version'                                         : ['Soll', true, ''],
             'Diagnosis-ID'                                        : ['MUSS', true, ''],
             'Progress-ID'                                         : ['MUSS', true, ''],
             'OPS-Code'                                            : ['MUSS', false, ''],
             'Surgery-ID'                                          : ['MUSS', true, ''],
             'Abwartende Strategie'                                : ['MUSS', false, ''],
             'Systemische Therapie Substanzen'                     : ['MUSS', false, ''],
             'Systemische Therapie Protokoll'                      : ['MUSS', false, ''],
             'ICD-O Katalog Morphologie (Version)'                 : ['MUSS', true, ''],
             'TNM-ID'                                              : ['MUSS', true, ''],
             'TNM-N'                                               : ['MUSS', false, ''],
             'Todesursachen'                                       : ['MUSS', false, ''],
             'TNM-M'                                               : ['MUSS', false, ''],
             'Lokalisation Fernmetastasen'                         : ['MUSS', false, ''],
             'Knochenmarktransplantation'                          : ['MUSS', false, ''],
             'Geschlecht'                                          : ['', false, ''],
             'Grading'                                             : ['', false, ''],
             'Datum des letztbekannten Vitalstatus'                : ['MUSS', true, ''],
             'TNM-T'                                               : ['MUSS', false, ''],
             'ICD-O Katalog Topographie (Version)'                 : ['MUSS', true, ''],
             'Datum der TNM Dokumentation/Datum Befund'            : ['MUSS', true, ''],
             'Lokalisation'                                        : ['MUSS', false, ''],
             'Hormontherapie'                                      : ['MUSS', false, ''],
             'Vitalstatus'                                         : ['MUSS', false, ''],
             'Datum Fernmetastasen'                                : ['MUSS', true, ''],
             'Gesamtbeurteilung Resttumor'                         : ['', false, ''],
             'Therapieart'                                         : ['', false, ''],
             'Histology-ID'                                        : ['MUSS', true, ''],
             'Tod tumorbedingt'                                    : ['MUSS', false, ''],
             'Patient-ID'                                          : ['', true, ''],
             'Intention OP'                                        : ['', false, ''],
             'Intention Chemotherapie'                             : ['', false, ''],
             'TNM-y-Symbol'                                        : ['', false, ''],
             'TNM-m-Symbol'                                        : ['', false, ''],
             'DKTK-ID-Global'                                      : ['', true, '']]
    dataModel.putElement(attributeMetaInfo, attributeMetaInfoKey)
%>
<%
    def patientIdDktkLokalIdMap = [:]
    dataModel.getSourcePaths().forEach(path -> {
        if (dataModel.hasOnlyHeaders(path)) {
            def patients = [] as Set
            dataModel.fetchHeaders(path).forEach { attribute ->
                if (attribute != null && attribute.trim().size() > 0) {
                    dataModel.putElement(patients, patientsProAttributeValueKey, attribute, emptyValue)
                    dataModel.putElement(patients, patientsProAttributeKey, attribute, emptyValue)
                }
            }
        } else {
            dataModel.applyToRecords(path, csvRecord -> {
                def patientId = csvRecord.get("Patient-ID")
                if (path.getFileName().toString().contains("Patient")){
                    def dktkLokalId = csvRecord.get("DKTK-ID-Lokal")
                    if (dktkLokalId != null && dktkLokalId.trim().size()>0){
                        patientIdDktkLokalIdMap.put(patientId, dktkLokalId)
                    }
                }
                csvRecord.getParser().getHeaderMap().keySet().forEach(header -> {
                    def value = csvRecord.get(header)

                    if (!header.toLowerCase().contains("validation")) {
                        if (value == null || value.trim().size() == 0) {
                            value = emptyValue
                        }
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
                }
                )
            })
        }
    })
    dataModel.putElement(patientIdDktkLokalIdMap, patientIdDktkLokalIdMapKey)
%>
<%
    def patientIds = [] as Set
    dataModel.getAllElement(patientsProAttributeKey).forEach { tempPatientIds -> patientIds.addAll(tempPatientIds) }
    def totalNumberOfPatients = Integer.valueOf(patientIds.size())
    dataModel.putElement(totalNumberOfPatients, totalNumberOfPatientsKey)
%>
