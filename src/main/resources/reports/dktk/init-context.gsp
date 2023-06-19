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
0. MUSS / Soll / kann
1. To be filtered in sheet 'filtered elements' (boolean)
2. Profile-Link
3. Value Set-Link
4. MDR-ID
5. Description
6. Data Type
7. DKTK-ID
-->
<%
    def attributeMetaInfo =
            ["Gesamtbeurteilung Tumorstatus" : ["Soll", "false", "https://simplifier.net/oncology/gesamtbeurteilungtumorstatus", "https://simplifier.net/oncology/gesamtbeurteilungtumorstatusvs", "urn:dktk:dataelement:84:2", "Gesamtbeurteilung des Tumorstatus. Für den Fall, dass nur eine Primärtherapie gegeben wurde, wird 'entspricht Primärtherapie' angezeigt. Es werden nur die Patienten ausgegeben, deren Dokumentation des Ansprechens innerhalb von 3 Monaten liegt", "Catalog", "K-53"],
             "Geschlecht" : ["MUSS", "false", "https://simplifier.net/oncology/patient", "https://simplifier.net/packages/hl7.fhir.r4.core/4.0.1/files/81193", "urn:dktk:dataelement:1:3", "Das Geschlecht des Patienten", "Catalog", "A-2"],
             "Geburtsdatum" : ["MUSS", "true", "https://simplifier.net/oncology/patient", "", "urn:dktk:dataelement:26:4", "Das Geburtsdatum des Patienten, falls der Tag oder Monat unbekannt ist 00 einsetzen", "Date", "A-1"],
             "DKTK-ID-Global" : ["kann", "true", "https://simplifier.net/oncology/patientenpseudonym", "", "urn:dktk:dataelement:54:1", "Die zentral generierte DKTK-ID", "String", "A-0"],
             "Untersuchungs-, Befunddatum im Verlauf" : ["MUSS", "true", "https://simplifier.net/oncology/verlauf", "", "urn:dktk:dataelement:25:4", "Dieses Feld ist notwendig für die korrekte Selektion ggf. mehrerer vorliegender Therapien durch den Standort", "Date", "K-46"],
             "Datum lokales oder regionäres Rezidiv" : ["MUSS", "true", "https://simplifier.net/oncology/lokalertumorstatus", "", "urn:dktk:dataelement:43:3", "Entspricht dem Datum, an welchem dieses Ereignis befundet wurde", "Date", "K-48"],
             "Datum Fernmetastasen" : ["MUSS", "true", "https://simplifier.net/oncology/fernmetastasen-duplicate-2", "", "urn:dktk:dataelement:46:3", "Entspricht dem Datum, an welchem dieses Ereignis befundet wurde", "Date", "K-52"],
             "Lokales oder regionäres Rezidiv" : ["MUSS", "false", "https://simplifier.net/oncology/lokalertumorstatus", "https://simplifier.net/oncology/verlauflokalertumorstatusvs", "urn:dktk:dataelement:72:2", "Beurteilung der Situation im Primärtumorbereich", "Catalog", "K-47"],
             "Lymphknoten-Rezidiv" : ["MUSS", "false", "https://simplifier.net/oncology/tumorstatuslymphknoten", "https://simplifier.net/oncology/verlauftumorstatuslymphknotenvs", "urn:dktk:dataelement:73:2", "Beurteilung der Situation im Bereich der regionären Lymphknoten", "Catalog", "K-49"],
             "Fernmetastasen" : ["MUSS", "false", "https://simplifier.net/oncology/tumorstatusfernmetastasen", "https://simplifier.net/oncology/verlauftumorstatusfernmetastasenvs", "urn:dktk:dataelement:74:2", "Beurteilung der Situation im Bereich der Fernmetastasen", "Catalog", "K-51"],
             "Lokale Beurteilung Resttumor" : ["Soll", "false", "https://simplifier.net/oncology/operation", "https://simplifier.net/oncology/lokalebeurteilungresidualstatusvs", "urn:dktk:dataelement:19:2", "Gibt die lokale Beurteilung (R-Klassifikation lokal) des zurückgebliebenen Resttumors nach Resektion meist des Primärtumors aber z.B. auch Lebermetastasen an", "Catalog", "K-23"],
             "Gesamtbeurteilung Resttumor" : ["Soll", "false", "https://simplifier.net/oncology/operation", "https://simplifier.net/oncology/gesamtbeurteilungresidualstatusvs", "urn:dktk:dataelement:20:3", "Gibt die Gesamtbeurteilung (R-Klassifikation global) des zurückgebliebenen Resttumors einschließlich etwaiger Fernmetastasen an", "Catalog", "K-24"],
             "Primärdiagnose" : ["MUSS", "true", "https://simplifier.net/oncology/primaerdiagnose", "", "urn:dktk:dataelement:29:2", "Kodierung der Erkrankung/Diagnose des Patienten anhand der aktuellen ICD-Klassifizierung", "String", "K-4"],
             "Version des ICD-10-Katalogs" : ["kann", "true", "https://simplifier.net/oncology/primaerdiagnose", "", "urn:dktk:dataelement:3:2", "Katalogversion der ICD", "String", "K-5"],
             "Lokalisation" : ["MUSS", "true", "https://simplifier.net/oncology/primaerdiagnose", "", "urn:dktk:dataelement:4:2", "Bezeichnung der Topographie einer Erkrankung basierend auf der aktuellen ICD-O-3 Klassifizierung", "String", "K-6"],
             "ICD-O Katalog Topographie (Version)" : ["kann", "true", "https://simplifier.net/oncology/primaerdiagnose", "", "urn:dktk:dataelement:5:2", "Katalogversion der ICD-O", "String", "K-7"],
             "Seitenlokalisation nach ADT-GEKID" : ["MUSS", "false", "https://simplifier.net/oncology/primaerdiagnose", "https://simplifier.net/oncology/seitenlokalisationvs", "urn:dktk:dataelement:6:2", "Organspezifische Angabe der betroffenen Seite", "Catalog", "K-8"],
             "Morphologie" : ["MUSS", "true", "https://simplifier.net/oncology/histologie", "", "urn:dktk:dataelement:7:2", "Gibt an, welche Histologie der Tumor aufweist, basierend auf der aktuellen ICD-O-3 Klassifizierung", "String", "K-9"],
             "Fernmetastasen vorhanden" : ["MUSS", "false", "https://simplifier.net/oncology/fernmetastasen-duplicate-2", "https://simplifier.net/oncology/jnuvs", "urn:dktk:dataelement:77:1", "Gibt an, ob ein positiver Befund von Fernmetastasen vorliegt", "Catalog", "K-25"],
             "ICD-O Katalog Morphologie (Version)" : ["kann", "true", "https://simplifier.net/oncology/histologie", "", "urn:dktk:dataelement:8:2", "Katalogversion der ICD-O", "String", "K-10"],
             "Tumor Diagnosedatum" : ["MUSS", "true", "https://simplifier.net/oncology/primaerdiagnose", "", "urn:dktk:dataelement:83:3", "Das Datum an dem die meldepflichtige Diagnose erstmals durch einen Arzt klinisch oder mikroskopisch diagnostiziert wurde", "Date", "K-2"],
             "UICC Stadium" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/uiccstadiumvs", "urn:dktk:dataelement:89:1", "Gibt das Stadium des Tumors (nach 'Union internationale contre le cancer') an", "Catalog", "K-12"],
             "Grading" : ["MUSS", "false", "https://simplifier.net/oncology/grading", "https://simplifier.net/oncology/gradingvs", "urn:dktk:dataelement:9:2", "Gibt den Differenzierungsgrad des Tumors an", "Catalog", "K-11"],
             "Lokalisation Fernmetastasen" : ["MUSS", "false", "https://simplifier.net/oncology/fernmetastasen-duplicate-2", "https://simplifier.net/oncology/fmlokalisationvs", "urn:dktk:dataelement:98:1", "Gibt die Lokalisation der Fernmetastase an", "Catalog", "K-27"],
             "Intention OP" : ["MUSS", "false", "https://simplifier.net/oncology/operation", "https://simplifier.net/oncology/opintentionvs", "urn:dktk:dataelement:23:3", "Gibt die Intention der Operation an", "Catalog", "K-33"],
             "Chemotherapie" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "", "urn:dktk:dataelement:36:2", "Gibt an, ob der Tumor mittels Chemotherapie behandelt wurde", "Boolean", "K-37"],
             "Immuntherapie" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "", "urn:dktk:dataelement:38:2", "Gibt an, ob der Tumor mittels Immuntherapie behandelt wurde", "Boolean", "K-40"],
             "Hormontherapie" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "", "urn:dktk:dataelement:39:2", "Gibt an, ob der Tumor mittels Hormontherapie behandelt wurde", "Boolean", "K-41"],
             "Knochenmarktransplantation" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "", "urn:dktk:dataelement:40:2", "Gibt an, ob eine Knochenmarktransplantation durchgeführt wurde", "Boolean", "K-42"],
             "Intention Chemotherapie" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "https://simplifier.net/oncology/systintentionvs", "urn:dktk:dataelement:69:2", "Gibt die Intention der Chemotherapie an", "Catalog", "K-38"],
             "Systemische Therapie Stellung zu operativer Therapie" : ["MUSS", "false", "https://simplifier.net/oncology/systemtherapie", "https://simplifier.net/oncology/syststellungopvs", "urn:dktk:dataelement:70:3", "Gibt an, in welchem Bezug zu einer operativen Therapie die systemische Therapie steht", "Catalog", "K-39"],
             "TNM-m-Symbol" : ["kann", "true", "https://simplifier.net/oncology/tnmp", "", "urn:dktk:dataelement:10:2", "Gibt an, ob multiple Primärtumoren in einem Bezirk vorliegen", "String", "K-14"],
             "TNM-Version" : ["kann", "true", "https://simplifier.net/oncology/tnmp", "", "urn:dktk:dataelement:18:2", "Katalogversion des TNM", "String", "K-22"],
             "TNM-r-Symbol" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmrsymbolvs", "urn:dktk:dataelement:81:1", "Gibt an, ob es sich bei dem Tumor um ein beurteiltes Rezidiv handelt", "Catalog", "K-21"],
             "TNM-y-Symbol" : ["kann", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmysymbolvs", "urn:dktk:dataelement:82:1", "Gibt an, wann im Fall multimodaler Therapien mit vielfältigen Behandlungsansätzen die Klassifikation während oder nach diesen Therapien erfolgte", "Catalog", "K-20"],
             "c/p/u-Präfix M" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmcpupraefixmvs", "urn:dktk:dataelement:80:1", "Gibt an, ob die Klassifikation durch einen Arzt oder Pathologen erfolgt ist für M", "Catalog", "K-19"],
             "TNM-M" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmmvs", "urn:dktk:dataelement:99:1", "Gibt an, ob Metastasen vorliegen", "Catalog", "K-14"],
             "TNM-N" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmnvs", "urn:dktk:dataelement:101:1", "Gibt an, ob Lymphknoten befallen sind", "Catalog", "K-15"],
             "c/p/u-Präfix N" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmcpupraefixnvs", "urn:dktk:dataelement:79:1", "Gibt an, ob die Klassifikation durch einen Arzt oder Pathologen erfolgt ist für N", "Catalog", "K-18"],
             "TNM-T" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmtvs", "urn:dktk:dataelement:100:1", "Gibt die Ausdehnung (Ort und Größe) des Primärtumors an", "Catalog", "K-13"],
             "c/p/u-Präfix T" : ["MUSS", "false", "https://simplifier.net/oncology/tnmp", "https://simplifier.net/oncology/tnmcpupraefixtvs", "urn:dktk:dataelement:78:1", "Gibt an, ob die Klassifikation durch einen Arzt oder Pathologen erfolgt ist für T", "Catalog", "K-17"],
             "Datum des letztbekannten Vitalstatus" : ["MUSS", "true", "https://simplifier.net/oncology/vitalstatus", "", "urn:dktk:dataelement:48:3", "Entspricht dem Datum, an welchem dieser Verlauf zum Vitalstatus zuletzt dokumentiert wurde", "Date", "K-55"],
             "Vitalstatus" : ["MUSS", "false", "https://simplifier.net/oncology/vitalstatus", "https://simplifier.net/oncology/vitalstatusvs", "urn:dktk:dataelement:53:3", "Vitalstatus", "Catalog", "K-56"],
             "DKTK-ID-Lokal" : ["kann", "true", "https://simplifier.net/oncology/patientenpseudonym", "", "urn:dktk:dataelement:91:1", "Patient kann mehrere Pseudonyme haben", "String", ""],
             "Primaertumor Diagnosetext" : ["kann", "true", "https://simplifier.net/oncology/histologie", "", "urn:adt:dataelement:28:1", "Primärtumor Tumordiagnose Text", "String", ""],
             "Histologie Datum" : ["kann", "true", "https://simplifier.net/oncology/histologie", "", "urn:adt:dataelement:35:1", "Tumor Histologiedatum", "Date", ""],
             "Morphologie-Freitext" : ["kann", "true", "https://simplifier.net/oncology/histologie", "", "urn:adt:dataelement:39:1", "Morphologie-Freitext", "String", ""],
             "Datum der OP" : ["kann", "true", "https://simplifier.net/oncology/operation", "", "urn:adt:dataelement:69:1", "OP Datum", "Date", ""],
             "Systemische Therapie Protokoll" : ["kann", "true", "https://simplifier.net/oncology/systemtherapie", "", "urn:adt:dataelement:89:1", "Systemische Therapie Protokoll", "String", ""],
             "Systemische Therapie Beginn" : ["kann", "true", "https://simplifier.net/oncology/systemtherapie", "", "urn:adt:dataelement:90:1", "Systemische Therapie Beginn", "Date", ""],
             "Systemische Therapie Substanzen" : ["kann", "true", "https://simplifier.net/oncology/systemtherapie", "", "urn:adt:dataelement:91:1", "Systemische Therapie Substanzen", "String", ""],
             "Tod tumorbedingt" : ["kann", "false", "https://simplifier.net/oncology/todursache", "https://simplifier.net/oncology/jnuvs", "urn:adt:dataelement:104:1", "Tod tumorbedingt", "Catalog", ""],
             "Todesursachen" : ["kann", "false", "https://simplifier.net/oncology/todursache", "", "urn:adt:dataelement:105:1", "Todesursachen", "Catalog", ""],
             "Systemische Therapie Ende" : ["kann", "true", "https://simplifier.net/oncology/systemtherapie", "", "urn:adt:dataelement:93:1", "Systemische Therapie Ende", "Date", ""],
             "System-Therapy-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "Metastasis-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "Diagnosis-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "Progress-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "OPS-Code" : ["kann", "false", "", "", "", "", "Catalog", ""],
             "Surgery-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "TNM-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "Datum der TNM Dokumentation" : ["MUSS", "true", "https://simplifier.net/oncology/tnmp", "", "", "", "Date", ""],
             "Therapieart" : ["MUSS", "true", "", "", "", "", "String", ""],
             "Histology-ID" : ["kann", "true", "", "", "", "", "String", ""],
             "Patient-ID" : ["kann", "true", "", "", "", "", "String", ""]]

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
