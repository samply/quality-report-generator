package de.samply.qualityreportgenerator.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "quality-report")
public class QualityReportTemplate {

  @JacksonXmlProperty(isAttribute = true, localName = "id")
  @JsonProperty(value = "id")
  private String id;

  @JacksonXmlProperty(isAttribute = true, localName = "filename")
  @JsonProperty(value = "filename")
  private String filename;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("sheet")
  private List<SheetTemplate> sheetTemplates = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public List<SheetTemplate> getSheetTemplates() {
    return sheetTemplates;
  }

  public void setSheetTemplates(
      List<SheetTemplate> sheetTemplates) {
    this.sheetTemplates = sheetTemplates;
  }

}
