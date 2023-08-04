package de.samply.reporter.script;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.context.Context;
import de.samply.reporter.context.CsvConfig;
import de.samply.reporter.template.script.Script;
import de.samply.reporter.utils.FileUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ScriptEngineImpl implements ScriptEngine {

  @Override
  public ScriptResult generateRawResult(Script script, Context context)
      throws ScriptEngineException {
    return new ScriptResult(fetchResultPath(context), context.getCsvConfig());
  }

  private Path fetchResultPath(Context context) {
    return context.getResultsDirectory().resolve(fetchRandomFilename());
  }

  protected void removeUnnecessaryEmptyLines(ScriptResult result, CsvConfig csvConfig)
      throws ScriptEngineException {
    Path tempPath = result.rawResult().getParent().resolve(fetchRandomFilename());
    removeUnnecessaryEmptyLinesAndCopyToTempPath(result.rawResult(), tempPath, csvConfig);
    deleteFile(result.rawResult());
    renameFile(tempPath, result.rawResult());
  }

  private void deleteFile(Path path) throws ScriptEngineException {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private void renameFile(Path originalPath, Path targetPath) throws ScriptEngineException {
    try {
      Files.move(originalPath, targetPath);
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private void removeUnnecessaryEmptyLinesAndCopyToTempPath(Path originalPath, Path tempPath,
      CsvConfig csvConfig) throws ScriptEngineException {
    try (PrintWriter printWriter = new PrintWriter(new FileWriter(tempPath.toFile()))) {
      Files.readAllLines(originalPath).forEach(line -> {
        if (line.trim().length() > csvConfig.endOfLine().length()) {
          line = removeInitialSpaces(line);
          printWriter.print(line + csvConfig.endOfLine());
        }
      });
    } catch (IOException e) {
      throw new ScriptEngineException(e);
    }
  }

  private String removeInitialSpaces(String input) {
    int index = 0;
    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) == ' ') {
        index = i + 1;
      } else {
        break;
      }
    }
    return (index > 0 && index < input.length()) ? input.substring(index) : input;
  }

  protected String fetchRandomFilename() {
    return FileUtils.fetchRandomFilename(ReporterConst.DEFAULT_SCRIPT_RESULT_FILE_EXTENSION);
  }

}
