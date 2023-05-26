package de.samply.reporter.utils;

import de.samply.reporter.app.ReporterConst;
import org.apache.commons.lang3.RandomStringUtils;

public class FileUtils {

  public static String fetchRandomFilename(String fileFormatExtension) {
    return RandomStringUtils.random(ReporterConst.RANDOM_FILENAME_SIZE, true, false) + "."
        + fileFormatExtension;
  }


}
