package de.samply.reporter.exporter;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.logger.BufferedLoggerFactory;
import de.samply.reporter.logger.Logger;
import de.samply.reporter.template.Exporter;
import de.samply.reporter.template.ReportTemplate;
import de.samply.reporter.utils.ExportExpirationDate;
import de.samply.reporter.utils.FileUtils;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

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
    private final String exporterQueryContactId;
    private final String tempFilesDirectory;
    private final int maxNumberOfAttemptsToGetExport;
    private final int timeInSecondsToWaitBetweenAttemptsToGetExport;
    private final int webClientBufferSizeInBytes;
    private final Boolean isExporterInSameServer;
    private final Integer webClientRequestTimeoutInSeconds;
    private final Integer webClientConnectionTimeoutInSeconds;
    private final Integer webClientTcpKeepIdleInSeconds;
    private final Integer webClientTcpKeepIntervalInSeconds;
    private final Integer webClientTcpKeepConnetionNumberOfTries;
    private final ExportExpirationDate exportExpirationDate;

    public ExporterClient(@Value(ReporterConst.EXPORTER_URL_SV) String exporterUrl,
                          @Value(ReporterConst.EXPORTER_API_KEY_SV) String exporterApiKey,
                          @Value(ReporterConst.EXPORTER_QUERY_SV) String exporterQuery,
                          @Value(ReporterConst.EXPORTER_QUERY_FORMAT_SV) String exporterQueryFormat,
                          @Value(ReporterConst.EXPORTER_TEMPLATE_ID_SV) String exporterTemplateId,
                          @Value(ReporterConst.EXPORTER_OUTPUT_FORMAT_SV) String exporterOutputFormat,
                          @Value(ReporterConst.EXPORTER_QUERY_CONTACT_ID_SV) String exporterQueryContactId,
                          @Value(ReporterConst.TEMP_FILES_DIRECTORY_SV) String tempFilesDirectory,
                          @Value(ReporterConst.MAX_NUMBER_OF_ATTEMPTS_TO_GET_EXPORT_SV) Integer maxNumberOfAttemptsToGetExport,
                          @Value(ReporterConst.TIME_IN_SECONDS_TO_WAIT_BETWEEN_ATTEMPTS_TO_GET_EXPORT_SV) Integer timeInSecondsToWaitBetweenAttemptsToGetExport,
                          @Value(ReporterConst.WEBCLIENT_BUFFER_SIZE_IN_BYTES_SV) Integer webClientBufferSizeInBytes,
                          @Value(ReporterConst.WEBCLIENT_REQUEST_TIMEOUT_IN_SECONDS_SV) Integer webClientRequestTimeoutInSeconds,
                          @Value(ReporterConst.WEBCLIENT_CONNECTION_TIMEOUT_IN_SECONDS_SV) Integer webClientConnectionTimeoutInSeconds,
                          @Value(ReporterConst.WEBCLIENT_TCP_KEEP_IDLE_IN_SECONDS_SV) Integer webClientTcpKeepIdleInSeconds,
                          @Value(ReporterConst.WEBCLIENT_TCP_KEEP_INTERVAL_IN_SECONDS_SV) Integer webClientTcpKeepIntervalInSeconds,
                          @Value(ReporterConst.WEBCLIENT_TCP_KEEP_CONNECTION_NUMBER_OF_TRIES_SV) Integer webClientTcpKeepConnetionNumberOfTries,
                          @Value(ReporterConst.IS_EXPORTER_IN_SAME_SERVER_SV) Boolean isExporterInSameServer,
                          ExportExpirationDate exportExpirationDate) {
        this.webClientRequestTimeoutInSeconds = webClientRequestTimeoutInSeconds;
        this.webClientConnectionTimeoutInSeconds = webClientConnectionTimeoutInSeconds;
        this.webClientTcpKeepIdleInSeconds = webClientTcpKeepIdleInSeconds;
        this.webClientTcpKeepIntervalInSeconds = webClientTcpKeepIntervalInSeconds;
        this.webClientTcpKeepConnetionNumberOfTries = webClientTcpKeepConnetionNumberOfTries;
        this.exportExpirationDate = exportExpirationDate;
        this.isExporterInSameServer = isExporterInSameServer;
        this.exporterApiKey = exporterApiKey;
        this.exporterQuery = exporterQuery;
        this.exporterQueryFormat = exporterQueryFormat;
        this.exporterTemplateId = exporterTemplateId;
        this.exporterOutputFormat = exporterOutputFormat;
        this.exporterQueryContactId = exporterQueryContactId;
        this.tempFilesDirectory = tempFilesDirectory;
        this.maxNumberOfAttemptsToGetExport = maxNumberOfAttemptsToGetExport;
        this.timeInSecondsToWaitBetweenAttemptsToGetExport = timeInSecondsToWaitBetweenAttemptsToGetExport;
        this.webClientBufferSizeInBytes = webClientBufferSizeInBytes;
        this.webClient = createWebClient(exporterUrl);
    }

    private WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(webClientBufferSizeInBytes))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(webClientRequestTimeoutInSeconds))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConnectionTimeoutInSeconds * 1000)
                                .option(ChannelOption.SO_KEEPALIVE, true)
                                .option(EpollChannelOption.TCP_KEEPIDLE, webClientTcpKeepIdleInSeconds)
                                .option(EpollChannelOption.TCP_KEEPINTVL, webClientTcpKeepIntervalInSeconds)
                                .option(EpollChannelOption.TCP_KEEPCNT, webClientTcpKeepConnetionNumberOfTries)
                ))
                .baseUrl(baseUrl).build();
    }

    public void fetchExportFiles(Consumer<String> exportFilePathConsumer,
                                 ReportTemplate template, Runnable finalizer)
            throws ExporterClientException {
        RequestResponseEntity requestResponseEntity;
        if (template.getExporter() == null || template.getExporter().getExportUrl() == null) {
            logger.info("Sending request to exporter...");
            Exporter exporter = fetchExporter(template);
            RequestBodySpec requestBodySpec = webClient.post().uri(
                            uriBuilder -> uriBuilder.path(ReporterConst.EXPORTER_REQUEST)
                                    .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY, exporter.getQuery())
                                    .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY_FORMAT,
                                            exporter.getQueryFormat())
                                    .queryParamIfPresent(ReporterConst.EXPORTER_REQUEST_PARAM_TEMPLATE_ID,
                                            Optional.ofNullable(exporter.getTemplateId()))
                                    .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_OUTPUT_FORMAT,
                                            exporter.getOutputFormat())
                                    .queryParam(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY_CONTACT_ID, exporterQueryContactId)
                                    .queryParamIfPresent(ReporterConst.EXPORTER_REQUEST_PARAM_QUERY_EXPIRATION_DATE,
                                            exportExpirationDate.calculateExportExpirationDate(template.getExporter().getExportExpirationInDays()))
                                    .build())
                    .header(ReporterConst.HTTP_HEADER_API_KEY, exporterApiKey)
                    .header(ReporterConst.IS_INTERNAL_REQUEST, isExporterInSameServer.toString());
            if (StringUtils.hasText(exporter.getTemplate())) {
                requestBodySpec.contentType(MediaType.APPLICATION_XML);
                requestBodySpec.bodyValue(exporter.getTemplate());
            }
            requestResponseEntity = requestBodySpec.retrieve()
                    .bodyToMono(RequestResponseEntity.class).block();
        } else {
            requestResponseEntity = new RequestResponseEntity(template.getExporter().getExportUrl());
        }
        fetchExportFiles(requestResponseEntity, exportFilePathConsumer, finalizer);
    }

    private void fetchExportExpirationDate(int expirationDateInHours) {

    }

    private Exporter fetchExporter(ReportTemplate template) {
        Exporter exporter = new Exporter();
        exporter.setQuery(fetchExporterValue(template, Exporter::getQuery, exporterQuery));
        exporter.setQueryFormat(
                fetchExporterValue(template, Exporter::getQueryFormat, exporterQueryFormat));
        if (template != null && template.getExporter() != null && template.getExporter().getTemplate() != null) {
            exporter.setTemplate(fetchExporterValue(template, Exporter::getTemplate, null));
        } else {
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

    private void fetchExportFiles(RequestResponseEntity requestResponseEntity, Consumer<String> exportFilePathConsumer,
                                  Runnable finalizer) throws ExporterClientException {
        try {
            AtomicReference<String> filePath = new AtomicReference<>();
            fetchExportFiles(requestResponseEntity.responseUrl(), filePath)
                    .doOnError(throwable -> {
                        throw new RuntimeException(throwable);
                    })
                    .subscribe(fileBytes -> {
                        copyInputStreamToFilePath(new ByteArrayInputStream(fileBytes), filePath.get());
                        exportFilePathConsumer.accept(filePath.get());
                        finalizer.run();
                    }, throwable -> {
                        logger.error(ExceptionUtils.getStackTrace(throwable));
                        finalizer.run();
                    });

        } catch (RuntimeException e) {
            finalizer.run();
            throw new ExporterClientException(e);
        }
    }

    private Mono<byte[]> fetchExportFiles(String exportFilesUrl, AtomicReference<String> filePath) {
        /*logger.info("Fetching export... (Attempt: " + (
                maxNumberOfAttemptsToGetExport - counter.get() + 1) + ")");*/
        AtomicInteger counter = new AtomicInteger(1);
        return createWebClient(exportFilesUrl).get()
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        if (HttpStatus.OK.equals(clientResponse.statusCode())) {
                            logger.info("Export available. Downloading...");
                            filePath.set(fetchFilePath(fetchFilename(clientResponse)));
                            return clientResponse.bodyToMono(byte[].class);
                        } else {
                            return (counter.get() >= maxNumberOfAttemptsToGetExport) ?
                                    Mono.error(new ExporterClientException("Export file not ready after max number of attempts")) :
                                    Mono.error(new WebClientResponseException(clientResponse.statusCode(), null, clientResponse.headers().asHttpHeaders(), null, null, null));
                        }
                    } else {
                        return Mono.error(new ExporterClientException("Error getting export files: " + clientResponse.statusCode()));
                    }
                })
                .retryWhen(
                        Retry.fixedDelay(maxNumberOfAttemptsToGetExport, Duration.ofSeconds(timeInSecondsToWaitBetweenAttemptsToGetExport))
                                .filter(throwable -> shouldRetry(throwable, counter)));
    }

    private boolean shouldRetry(Throwable throwable, AtomicInteger counter) {
        if (throwable instanceof WebClientResponseException responseException) {
            logger.info("Fetching export... (Attempt: " + counter.getAndIncrement() + ")");
            HttpStatusCode statusCode = responseException.getStatusCode();
            // Retry if the status code is not 200 (indicating an error)
            return statusCode != HttpStatus.OK && isQueryStillRunning();
        }
        return false; // Do not retry for other types of exceptions
    }

    private boolean isQueryStillRunning() {
        //TODO
        return true;
    }

    private String fetchFilename(ClientResponse clientResponse) {
        List<String> header = clientResponse.headers()
                .header(ReporterConst.HTTP_HEADER_CONTENT_DISPOSITION);
        return (!header.isEmpty()) ? fetchFilenameFromHeader(header.get(0))
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
                    if (lastLine != null && !lastLine.isEmpty()) {
                        uriBuilder.queryParam(ReporterConst.EXPORTER_LOGS_LAST_LINE, lastLine);
                    }
                    return uriBuilder.build();
                })
                .header(ReporterConst.HTTP_HEADER_API_KEY, exporterApiKey)
                .retrieve()
                .bodyToMono(String[].class).block();
    }

}
