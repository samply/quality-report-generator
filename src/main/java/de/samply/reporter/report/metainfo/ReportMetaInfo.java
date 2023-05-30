package de.samply.reporter.report.metainfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.samply.reporter.app.ReporterConst;
import java.nio.file.Path;

public record ReportMetaInfo(
    @JsonProperty("id")
    String id,
    @JsonProperty("path") @JsonSerialize(using = PathSerializer.class)
    Path path,
    @JsonProperty("timestamp")
    String timestamp
) {

  @Override
  public String toString() {
    return id + ReporterConst.REPORT_META_INFO_FILE_SEPARATOR + path.getFileName().toString()
        + ReporterConst.REPORT_META_INFO_FILE_SEPARATOR + timestamp;
  }

  public static ReportMetaInfo create(Path reportDirectory, String reportMetaInfoLine) {
    String id = null;
    Path path = null;
    String timestamp = null;
    if (reportMetaInfoLine != null) {
      String[] split = reportMetaInfoLine.split(ReporterConst.REPORT_META_INFO_FILE_SEPARATOR);
      if (split.length > 0) {
        id = split[0];
      }
      if (split.length > 1) {
        path = reportDirectory.resolve(split[1]);
      }
      if (split.length > 2) {
        timestamp = split[2];
      }
    }
    return new ReportMetaInfo(id, path, timestamp);
  }

}
