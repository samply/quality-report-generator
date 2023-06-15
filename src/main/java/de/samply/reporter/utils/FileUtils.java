package de.samply.reporter.utils;

import de.samply.reporter.app.ReporterConst;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.RandomStringUtils;

public class FileUtils {

  public static String fetchRandomFilename(String fileFormatExtension) {
    return RandomStringUtils.random(ReporterConst.RANDOM_FILENAME_SIZE, true, false) + "."
        + fileFormatExtension;
  }

  public static long fetchNumberOfLines(Path path) throws IOException {
    return Files.lines(path).count();
  }


}
