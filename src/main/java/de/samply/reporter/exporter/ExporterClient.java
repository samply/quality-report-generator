package de.samply.reporter.exporter;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.logger.BufferedLoggerFactory;
import de.samply.reporter.logger.Logger;
import de.samply.reporter.template.Exporter;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.utils.FileUtils;
import io.netty.channel.ChannelOption;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class ExporterClient {

    private final static Logger logger = BufferedLoggerFactory.getLogger(ExporterClient.class);
    private final WebClient webClient;
    private final String exporterApiKey;
    private final String exporterQuery;
    private final String exporterQueryFormat;
    private final String exporterTemplateId;
    private final String exporterOutputFormat;
    private final String tempFilesDirectory;
    private final int maxNumberOfAttemptsToGetExport;
    private final int timeInSecondsToWaitBetweenAttemptsToGetExport;
    private final int webClientBufferSizeInBytes;

    public ExporterClient(@Value(ReporterConst.EXPORTER_URL_SV) String exporterUrl,
                          @Value(ReporterConst.EXPORTER_API_KEY_SV) String exporterApiKey,
                          @Value(ReporterConst.EXPORTER_QUERY_SV) String exporterQuery,
                          @Value(ReporterConst.EXPORTER_QUERY_FORMAT_SV) String exporterQueryFormat,
                          @Value(ReporterConst.EXPORTER_TEMPLATE_ID_SV) String exporterTemplateId,
                          @Value(ReporterConst.EXPORTER_OUTPUT_FORMAT_SV) String exporterOutputFormat,
                          @Value(ReporterConst.TEMP_FILES_DIRECTORY_SV) String tempFilesDirectory,
                          @Value(ReporterConst.MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT_SV) Integer maxNumberOfAttemptsToGetExport,
                          @Value(ReporterConst.TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT_SV) Integer timeInSecondsToWaitBetweenAttemptsToGetExport,
                          @Value(ReporterConst.WEBCLIENT_BUFFER_SIZE_IN_BYTES_SV) Integer webClientBufferSizeInBytes,
                          @Value(ReporterConst.WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS_SV) Integer webClientRequestTimeoutInSeconds,
                          @Value(ReporterConst.WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS_SV) Integer webClientConnectionTimeoutInSeconds) {
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(webClientRequestTimeoutInSeconds))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConnectionTimeoutInSeconds * 1000)
                ))
                .baseUrl(exporterUrl).build();
        this.exporterApiKey = exporterApiKey;
        this.exporterQuery = exporterQuery;
        this.exporterQueryFormat = exporterQueryFormat;
        this.exporterTemplateId = exporterTemplateId;
        this.exporterOutputFormat = exporterOutputFormat;
        this.tempFilesDirectory = tempFilesDirectory;
        this.maxNumberOfAttemptsToGetExport = maxNumberOfAttemptsToGetExport;
        this.timeInSecondsToWaitBetweenAttemptsToGetExport = timeInSecondsToWaitBetweenAttemptsToGetExport;
        this.webClientBufferSizeInBytes = webClientBufferSizeInBytes;
    }

    public void fetchExportFiles(Consumer<String> exportFilePathConsumer,
                                 ReportTemplate template)
            throws ExporterClientException {
        RequestResponseEntity requestResponseEntity = null;
        if (template.getExporter() == null || template.getExporter().getExportUrl() == null) {
            logger.info("Sending request to exporter...");
            Exporter exporter = fetchExporter(template);
            RequestBodySpec requestBodySpec = webClient.post().uri(
                    uriBuilder -> uriBuilder.path(ReporterConst.EXPORTER_REQUEST)
                            .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY, exporter.getQuery())
                            .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY_FORMAT,
                                    exporter.getQueryFormat())
                            .queryParamIfPresent(ReporterConst.EXPORTER_REQUEST_PARAM_TEMPLATE_ID,
                                    Optional.of(exporter.getTemplateId()))
                            .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_OUTPUT_FORMAT,
                                    exporter.getOutputFormat())
                            .build()).header(ReporterConst.HTTP_HEADER_API_KEY, exporterApiKey);
            if (exporter.getTemplate() != null && exporter.getTemplate().trim().length() > 0) {
                requestBodySpec.contentType(MediaType.APPLICATION_XML);
                requestBodySpec.bodyValue(exporter.getTemplate());
            }
            requestResponseEntity = requestBodySpec.retrieve()
                    .bodyToMono(RequestResponseEntity.class).block();
        } else {
            requestResponseEntity = new RequestResponseEntity(template.getExporter().getExportUrl());
        }
        fetchExportFiles(requestResponseEntity, exportFilePathConsumer);
    }

    private Exporter fetchExporter(ReportTemplate template) {
        Exporter exporter = new Exporter();
        exporter.setQuery(fetchExporterValue(template, Exporter::getQuery, exporterQuery));
        exporter.setQueryFormat(
                fetchExporterValue(template, Exporter::getQueryFormat, exporterQueryFormat));
        if (exporter.getTemplate() == null) {
            exporter.setTemplateId(
                    fetchExporterValue(template, Exporter::getTemplateId, exporterTemplateId));
        }
        exporter.setOutputFormat(
                fetchExporterValue(template, Exporter::getOutputFormat, exporterOutputFormat));
        return exporter;
    }

    private String fetchExporterValue(ReportTemplate template,
                                      Function<Exporter, String> templateFunction, String defaultValue) {
        return (template != null && templateFunction.apply(template.getExporter()) != null)
                ? templateFunction.apply(template.getExporter()) : defaultValue;
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
        logger.info("Fetching export... (Attempt: " + (
                maxNumberOfAttemptsToGetExport - counter.get() + 1) + ")");
        return WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(webClientBufferSizeInBytes))
                .baseUrl(exportFilesUrl).build().get()
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        if (!HttpStatus.OK.equals(clientResponse.statusCode())) {
                            waitUntilNextAttempt();
                            return (counter.decrementAndGet() >= 0) ? fetchExportFiles(exportFilesUrl, counter,
                                    filePath) : Mono.error(new ExporterClientException(
                                    "Export file not ready after max number of attempts"));
                        } else {
                            logger.info("Export available. Downloading...");
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
            Thread.sleep(timeInSecondsToWaitBetweenAttemptsToGetExport * 1000L);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private String fetchFilename(ClientResponse clientResponse) {
        List<String> header = clientResponse.headers()
                .header(ReporterConst.HTTP_HEADER_CONTENT_DISPOSITION);
        return (header.size() > 0) ? fetchFilenameFromHeader(header.get(0))
                : FileUtils.fetchRandomFilename(ReporterConst.DEFAULT_EXPORTER_FILE_EXTENSION);

    }

    private String fetchFilenameFromHeader(String headerField) {
        return (headerField != null && headerField.contains(
                ReporterConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME)) ? headerField.substring(
                headerField.indexOf(ReporterConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME)
                        + ReporterConst.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME.length()).replace("\"", "")
                : FileUtils.fetchRandomFilename(ReporterConst.DEFAULT_EXPORTER_FILE_EXTENSION);
    }

    private String fetchFilePath(String filename) {
        return Path.of(tempFilesDirectory).resolve(filename).toString();
    }

    private void copyInputStreamToFilePath(InputStream inputStream, String filePath) {
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(
                inputStream); FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] fetchLogs(int numberOfLines, String lastLine) {
        return this.webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(ReporterConst.EXPORTER_LOGS)
                            .queryParam(ReporterConst.EXPORTER_LOGS_SIZE, numberOfLines);
                    if (lastLine != null && lastLine.length() > 0) {
                        uriBuilder.queryParam(ReporterConst.EXPORTER_LOGS_LAST_LINE, lastLine);
                    }
                    return uriBuilder.build();
                })
                .header(ReporterConst.HTTP_HEADER_API_KEY, exporterApiKey)
                .retrieve()
                .bodyToMono(String[].class).block();
    }

}
