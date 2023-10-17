package de.samply.reporter.utils;

import de.samply.reporter.app.ReporterConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExportExpirationDate {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private final int defaultHoursToExpire;

    public ExportExpirationDate(
            @Value(ReporterConst.EXPORTER_HOURS_UNTIL_EXPIRATION_SV) Integer defaultHoursToExpire) {
        this.defaultHoursToExpire = defaultHoursToExpire;
    }

    public String calculateExportExpirationDate(Integer hoursToExpire) {
        if (hoursToExpire == null) {
            hoursToExpire = defaultHoursToExpire;
        }
        return LocalDateTime.now().plusHours(hoursToExpire).format(formatter);
    }

}
