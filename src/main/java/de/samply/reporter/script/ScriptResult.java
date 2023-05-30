package de.samply.reporter.script;

import de.samply.reporter.context.CsvConfig;
import java.nio.file.Path;

public class ScriptResult {

  private Path rawResult;
  private CsvConfig csvConfig;

  public ScriptResult(Path rawResult, CsvConfig csvConfig) {
    this.rawResult = rawResult;
    this.csvConfig = csvConfig;
  }

  public Path getRawResult() {
    return rawResult;
  }

  public CsvConfig getCsvConfig() {
    return csvConfig;
  }

}
