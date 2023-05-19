package de.samply.qualityreportgenerator.template.script;

public enum ScriptFramework {
  THYMELEAF("thymeleaf");

  private String framework;

  ScriptFramework(String framework) {
    this.framework = framework;
  }

  public String getStartTag() {
    return "<" + framework + ">";
  }

  public String getEndTag() {
    return "</" + framework + ">";
  }

  public String getFramework() {
    return framework;
  }

}
