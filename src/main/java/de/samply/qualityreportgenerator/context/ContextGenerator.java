package de.samply.qualityreportgenerator.context;

import de.samply.qualityreportgenerator.app.QrgConst;
import de.samply.qualityreportgenerator.template.QualityReportTemplate;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContextGenerator {

  private Path directory;
  private CsvConfig defaultCsvConfig;

  public ContextGenerator(
      @Value(QrgConst.TEMP_FILES_DIRECTORY_SV) String directory,
      @Value(QrgConst.FILE_CHARSET_SV) String charsetName,
      @Value(QrgConst.FILE_END_OF_LINE_SV) String endOfLine,
      @Value(QrgConst.DEFAULT_CSV_DELIMITER) String delimiter) {
    this.directory = Path.of(directory);
    Charset charset =
        (charsetName != null) ? Charset.forName(charsetName) : QrgConst.DEFAULT_CHARSET;
    endOfLine = (endOfLine != null) ? endOfLine : QrgConst.DEFAULT_END_OF_LINE;
    delimiter = (delimiter != null) ? delimiter : QrgConst.DEFAULT_END_OF_LINE;
    defaultCsvConfig = new CsvConfig(charset, endOfLine, delimiter);
  }

  public Context generate(QualityReportTemplate template, Path[] paths) {
    return new Context(directory, template, paths, defaultCsvConfig);
  }

}
