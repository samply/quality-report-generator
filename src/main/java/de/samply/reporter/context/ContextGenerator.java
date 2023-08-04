package de.samply.reporter.context;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.template.ReportTemplate;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContextGenerator {

  private final Path directory;
  private final CsvConfig defaultCsvConfig;

  public ContextGenerator(
      @Value(ReporterConst.TEMP_FILES_DIRECTORY_SV) String directory,
      @Value(ReporterConst.FILE_CHARSET_SV) String charsetName,
      @Value(ReporterConst.FILE_END_OF_LINE_SV) String endOfLine,
      @Value(ReporterConst.DEFAULT_CSV_DELIMITER) String delimiter) {
    this.directory = Path.of(directory);
    Charset charset =
        (charsetName != null) ? Charset.forName(charsetName) : ReporterConst.DEFAULT_CHARSET;
    endOfLine = (endOfLine != null) ? endOfLine : ReporterConst.DEFAULT_END_OF_LINE;
    delimiter = (delimiter != null) ? delimiter : ReporterConst.DEFAULT_END_OF_LINE;
    defaultCsvConfig = new CsvConfig(charset, endOfLine, delimiter);
  }

  public Context generate(ReportTemplate template, Path[] paths) {
    return new Context(directory, template, paths, defaultCsvConfig);
  }

}
