package de.samply.reporter.logger;

public record Logs(
        String source,
        String[] lastLines
) {

}
