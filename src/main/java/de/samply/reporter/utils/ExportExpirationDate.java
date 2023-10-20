package de.samply.reporter.utils;

import de.samply.reporter.app.ReporterConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ExportExpirationDate {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private final int defaultDaysToExpire;

    public ExportExpirationDate(
            @Value(ReporterConst.EXPORTER_DAYS_UNTIL_EXPIRATION_SV) Integer defaultDaysToExpire) {
        this.defaultDaysToExpire = defaultDaysToExpire;
    }

    public Optional<String> calculateExportExpirationDate(Integer daysToExpire) {
        if (daysToExpire == null) {
            daysToExpire = defaultDaysToExpire;
        }
        return (daysToExpire == ReporterConst.EXPORT_NOT_EXPIRES) ? Optional.empty() :
                Optional.of(LocalDateTime.now().plusDays(daysToExpire).format(formatter));
    }

}
