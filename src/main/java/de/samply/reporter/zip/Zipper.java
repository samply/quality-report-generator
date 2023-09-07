package de.samply.reporter.zip;

import de.samply.reporter.logger.BufferedLoggerFactory;
import de.samply.reporter.logger.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.InputStreamResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private final static Logger logger = BufferedLoggerFactory.getLogger(Zipper.class);

    public static Path zip(List<Path> paths) throws ZipperException {
        Path zippedFile = generateZipFilename(paths);
        zipFiles(paths, zippedFile);
        removeFiles(paths);
        return zippedFile;
    }

    private static void removeFiles (List<Path> paths){
        paths.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private static Path generateZipFilename(List<Path> paths){
        Path path = paths.get(0);
        String filename = path.getFileName().toString();
        int index = filename.lastIndexOf(".");
        filename = filename.substring(0, index);
        return path.getParent().resolve(filename + ".zip");
    }

    private static void zipFiles (List<Path> paths, Path zippedPath) throws ZipperException {
        try {
            zipFilesWithoutExceptionHandling(zippedPath, paths);
        } catch (FileNotFoundException e) {
            throw new ZipperException(e);
        }
    }

    private static InputStreamResource zipFilesWithoutExceptionHandling(Path zipFilePath,
                                                                 List<Path> filePaths) throws FileNotFoundException, ZipperException {
        try (FileOutputStream outputStream = new FileOutputStream(
                zipFilePath.toFile()); ZipOutputStream zipOutputStream = new ZipOutputStream(
                outputStream)) {
            addFilesToZipOutputStream(zipOutputStream, filePaths);
            return new InputStreamResource(new FileInputStream(zipFilePath.toFile()));
        } catch (IOException e) {
            throw new ZipperException(e);
        }
    }

    private static void addFilesToZipOutputStream(ZipOutputStream zipOutputStream, List<Path> filePaths)
            throws ZipperException {
        try {
            filePaths.forEach(filePath -> {
                File fileToZip = filePath.toFile();
                try (FileInputStream fileInputStream = new FileInputStream(fileToZip)) {
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            throw new ZipperException(e);
        }
    }


}
