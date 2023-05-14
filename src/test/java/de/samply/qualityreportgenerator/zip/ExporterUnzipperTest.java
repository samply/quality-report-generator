package de.samply.qualityreportgenerator.zip;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class ExporterUnzipperTest {

  private final String zipFile = "./temp-files/exporter-files-20230514-07_57.zip";
  private final String tempDirectory = "test-files";
  @Test
  void extractFiles() throws ExporterUnzipperException {
    ExporterUnzipper exporterUnzipper = new ExporterUnzipper(tempDirectory);
    exporterUnzipper.extractFiles(zipFile);
  }
}
