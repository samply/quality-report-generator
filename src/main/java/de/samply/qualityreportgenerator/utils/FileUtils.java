package de.samply.qualityreportgenerator.utils;

import de.samply.qualityreportgenerator.app.QrgConst;
import org.apache.commons.lang3.RandomStringUtils;

public class FileUtils {

  public static String fetchRandomFilename(String fileFormatExtension) {
    return RandomStringUtils.random(QrgConst.RANDOM_FILENAME_SIZE, true, false) + "."
        + fileFormatExtension;
  }


}
