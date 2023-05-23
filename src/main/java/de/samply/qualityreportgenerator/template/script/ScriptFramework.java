package de.samply.qualityreportgenerator.template.script;

public enum ScriptFramework {
  THYMELEAF("thymeleaf"),
  GROOVY_TEMPLATES("groovy");

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

  public static ScriptFramework getDefault() {
    return THYMELEAF;
  }

  public static ScriptFramework valueOfFramework(String framework) {
    for (ScriptFramework e : values()) {
      if (e.framework.equals(framework)) {
        return e;
      }
    }
    return null;
  }

}
