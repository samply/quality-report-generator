package de.samply.reporter.report;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RunningReportsManager {

    private Set<String> runningReportIds = new HashSet<>();


    public synchronized void addRunningReportId(String reportId) {
        runningReportIds.add(reportId);
    }

    public synchronized void removeRunningReportId(String reportId) {
        runningReportIds.remove(reportId);
    }

    public synchronized boolean isReportIdRunning(String reportId) {
        return runningReportIds.contains(reportId);
    }


}
