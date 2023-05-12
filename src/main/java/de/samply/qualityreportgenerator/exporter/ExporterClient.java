package de.samply.qualityreportgenerator.exporter;

import de.samply.qualityreportgenerator.app.QrgConst;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
public class ExporterClient {

  private final static Logger logger = LoggerFactory.getLogger(ExporterClient.class);
  private WebClient webClient;
  private final String exporterApiKey;
  private final String exporterQuery;
  private final String exporterQueryFormat;
  private final String exporterTemplateId;
  private final String exporterOutputFormat;
  private final String tempFilesDirectory;
  private final int maxNumberOfAttemptsToGetExport;
  private final int timeInSecondsToWaitBetweenAttemptsToGetExport;

  public ExporterClient(@Value(QrgConst.EXPORTER_URL_SV) String exporterUrl,
      @Value(QrgConst.EXPORTER_API_KEY_SV) String exporterApiKey,
      @Value(QrgConst.EXPORTER_QUERY_SV) String exporterQuery,
      @Value(QrgConst.EXPORTER_QUERY_FORMAT_SV) String exporterQueryFormat,
      @Value(QrgConst.EXPORTER_TEMPLATE_ID_SV) String exporterTemplateId,
      @Value(QrgConst.EXPORTER_OUTPUT_FORMAT_SV) String exporterOutputFormat,
      @Value(QrgConst.TEMP_FILES_DIRECTORY_SV) String tempFilesDirectory,
      @Value(QrgConst.MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT_SV) Integer maxNumberOfAttemptsToGetExport,
      @Value(QrgConst.TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT_SV) Integer timeInSecondsToWaitBetweenAttemptsToGetExport) {
    this.webClient = WebClient.builder().baseUrl(exporterUrl).build();
    this.exporterApiKey = exporterApiKey;
    this.exporterQuery = exporterQuery;
    this.exporterQueryFormat = exporterQueryFormat;
    this.exporterTemplateId = exporterTemplateId;
    this.exporterOutputFormat = exporterOutputFormat;
    this.tempFilesDirectory = tempFilesDirectory;
    this.maxNumberOfAttemptsToGetExport = maxNumberOfAttemptsToGetExport;
    this.timeInSecondsToWaitBetweenAttemptsToGetExport = timeInSecondsToWaitBetweenAttemptsToGetExport;
  }

  public void fetchExportFiles(Consumer<String> exportFilePathConsumer)
      throws ExporterClientException {
    RequestResponseEntity requestResponseEntity = webClient.post().uri(
            uriBuilder -> uriBuilder.path(QrgConst.EXPORTER_REQUEST)
                .queryParam(QrgConst.EXPORTER_REQUEST_PARAM_QUERY, exporterQuery)
                .queryParam(QrgConst.EXPORTER_REQUEST_PARAM_QUERY_FORMAT, exporterQueryFormat)
                .queryParam(QrgConst.EXPORTER_REQUEST_PARAM_TEMPLATE_ID, exporterTemplateId)
                .queryParam(QrgConst.EXPORTER_REQUEST_PARAM_OUTPUT_FORMAT, exporterOutputFormat)
                .build()).header(QrgConst.HTTP_HEADER_API_KEY, exporterApiKey).retrieve()
        .bodyToMono(RequestResponseEntity.class).block();
    fetchExportFiles(requestResponseEntity, exportFilePathConsumer);
  }

  private void fetchExportFiles(RequestResponseEntity requestResponseEntity,
      Consumer<String> exportFilePathConsumer) throws ExporterClientException {
    try {
      AtomicReference<String> filePath = new AtomicReference<>();
      fetchExportFiles(requestResponseEntity.responseUrl(), filePath).subscribe(fileBytes -> {
        copyInputStreamToFilePath(new ByteArrayInputStream(fileBytes), filePath.get());
        exportFilePathConsumer.accept(filePath.get());
      });
    } catch (RuntimeException e) {
      throw new ExporterClientException(e);
    }
  }

  private Mono<byte[]> fetchExportFiles(String exportFilesUrl, AtomicReference<String> filePath) {
    return fetchExportFiles(exportFilesUrl, new AtomicInteger(maxNumberOfAttemptsToGetExport),
        filePath);
  }

  private Mono<byte[]> fetchExportFiles(String exportFilesUrl, AtomicInteger counter,
      AtomicReference<String> filePath) {
    return WebClient.builder().baseUrl(exportFilesUrl).build().get()
        .exchangeToMono(clientResponse -> {
          if (clientResponse.statusCode().is2xxSuccessful()) {
            if (!HttpStatus.OK.equals(clientResponse.statusCode())) {
              waitUntilNextAttempt();
              return (counter.decrementAndGet() >= 0) ? fetchExportFiles(exportFilesUrl, counter,
                  filePath) : Mono.error(new ExporterClientException(
                  "Export file not ready after max number of attempts"));
            } else {
              filePath.set(fetchFilePath(fetchFilename(clientResponse)));
              return clientResponse.bodyToMono(byte[].class);
            }
          } else {
            return Mono.error(new ExporterClientException(
                "Error getting export files: " + clientResponse.statusCode()));
          }
        });
  }

  private void waitUntilNextAttempt() {
    try {
      Thread.sleep(timeInSecondsToWaitBetweenAttemptsToGetExport * 1000);
    } catch (InterruptedException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    }
  }

  private String fetchFilename(ClientResponse clientResponse) {
    List<String> header = clientResponse.headers().header(QrgConst.HTTP_HEADER_CONTENT_DISPOSITION);
    return (header != null && header.size() > 0) ? fetchFilenameFromHeader(header.get(0))
        : fetchRandomFilename();

  }

  private String fetchFilenameFromHeader(String headerField) {
    return (headerField != null && headerField.contains(
        QrgConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME)) ? headerField.substring(
        headerField.indexOf(QrgConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME)
            + QrgConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME.length()).replace("\"", "")
        : fetchRandomFilename();
  }

  private String fetchFilePath(String filename) {
    return Path.of(tempFilesDirectory).resolve(filename).toString();
  }

  private String fetchRandomFilename() {
    return RandomStringUtils.random(QrgConst.RANDOM_FILENAME_SIZE, true, false) + ".zip";
  }

  private void copyInputStreamToFilePath(InputStream inputStream, String filePath) {
    try (ReadableByteChannel readableByteChannel = Channels.newChannel(
        inputStream); FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
      fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
