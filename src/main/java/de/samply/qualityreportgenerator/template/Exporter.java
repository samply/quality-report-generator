package de.samply.qualityreportgenerator.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Exporter {

  @JacksonXmlProperty(isAttribute = true, localName = "query")
  @JsonProperty(value = "query")

  private String query;

  @JacksonXmlProperty(isAttribute = true, localName = "query-format")
  @JsonProperty(value = "query-format")

  private String queryFormat;

  @JacksonXmlProperty(isAttribute = true, localName = "template-id")
  @JsonProperty(value = "template-id")

  private String templateId;

  @JacksonXmlProperty(isAttribute = true, localName = "output-format")
  @JsonProperty(value = "output-format")

  private String outputFormat;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getQueryFormat() {
    return queryFormat;
  }

  public void setQueryFormat(String queryFormat) {
    this.queryFormat = queryFormat;
  }

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public String getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

}
