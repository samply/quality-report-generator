package de.samply.qualityreportgenerator.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ColumnTemplate {

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  @JsonProperty(value = "name")
  private String name;

  @JacksonXmlProperty(isAttribute = true, localName = "header-format-script")
  @JsonProperty(value = "header-format-script")
  private String headerFormatScript;

  @JacksonXmlProperty(isAttribute = true, localName = "value-format-script")
  @JsonProperty(value = "value-format-script")
  private String valueFormatScript;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHeaderFormatScript() {
    return headerFormatScript;
  }

  public void setHeaderFormatScript(String headerFormatScript) {
    this.headerFormatScript = headerFormatScript;
  }

  public String getValueFormatScript() {
    return valueFormatScript;
  }

  public void setValueFormatScript(String valueFormatScript) {
    this.valueFormatScript = valueFormatScript;
  }

}
