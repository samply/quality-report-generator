package de.samply.reporter.script;

import de.samply.reporter.context.CsvConfig;
import java.nio.file.Path;

public record ScriptResult(Path rawResult, CsvConfig csvConfig) {

}
