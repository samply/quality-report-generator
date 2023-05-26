package de.samply.reporter.zip;

import de.samply.reporter.app.ReporterConst;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExporterUnzipper {

  private Path tempFilesDirectory;

  public ExporterUnzipper(
      @Value(ReporterConst.TEMP_FILES_DIRECTORY_SV) String tempFilesDirectory) {
    this.tempFilesDirectory = Path.of(tempFilesDirectory);
  }

  public Path[] extractFiles(String zipPath) throws ExporterUnzipperException {
    Path zipPath2 = moveToTempDirectory(zipPath);
    unzipFiles(zipPath2);
    removePath(zipPath2);
    return fetchFilesFromDirectory(zipPath2.getParent());
  }

  private void unzipFiles(Path zipPath) throws ExporterUnzipperException {
    try {
      unzipFilesWithoutExceptionHandling(zipPath);
    } catch (IOException e) {
      throw new ExporterUnzipperException(e);
    }
  }

  private void unzipFilesWithoutExceptionHandling(Path zipPath) throws IOException {
    try (ZipInputStream zipInputStream = new ZipInputStream(
        new FileInputStream(zipPath.toFile()))) {
      ZipEntry zipEntry = zipInputStream.getNextEntry();
      while (zipEntry != null) {
        unzipFile(zipPath.getParent(), zipEntry, zipInputStream);
        zipInputStream.closeEntry();
        zipEntry = zipInputStream.getNextEntry();
      }
    }
  }

  private void unzipFile(Path directory, ZipEntry zipEntry, ZipInputStream zipInputStream)
      throws IOException {
    try (FileOutputStream fileOutputStream =
        new FileOutputStream(directory.resolve(zipEntry.getName()).toFile())) {
      int length;
      byte[] buffer = new byte[1024];
      while ((length = zipInputStream.read(buffer)) > 0) {
        fileOutputStream.write(buffer, 0, length);
      }
    }
  }

  private Path moveToTempDirectory(String zipPath) throws ExporterUnzipperException {
    try {
      Path originalPath = Path.of(zipPath);
      return Files.move(originalPath,
          createTempDirectory().resolve(originalPath.getFileName().toString()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new ExporterUnzipperException(e);
    }
  }

  private Path createTempDirectory() throws ExporterUnzipperException {
    try {
      return Files.createTempDirectory(tempFilesDirectory, ReporterConst.TEMP_DIRECTORY_PREFIX);
    } catch (IOException e) {
      throw new ExporterUnzipperException(e);
    }
  }

  private void removePath(Path path) throws ExporterUnzipperException {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new ExporterUnzipperException(e);
    }
  }

  private Path[] fetchFilesFromDirectory(Path directory) throws ExporterUnzipperException {
    try {
      return Files.walk(directory).filter(path -> !Files.isDirectory(path)).toArray(Path[]::new);
    } catch (IOException e) {
      throw new ExporterUnzipperException(e);
    }
  }


}
