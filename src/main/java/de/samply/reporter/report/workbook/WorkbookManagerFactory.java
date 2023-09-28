package de.samply.reporter.report.workbook;

import de.samply.reporter.app.ReporterConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WorkbookManagerFactory {

    private final Integer workbookWindow;
    private final Integer maxNumberOfRows;

    public WorkbookManagerFactory(
            @Value(ReporterConst.EXCEL_WORKBOOK_WINDOW_SV) Integer workbookWindow,
            @Value(ReporterConst.MAX_NUMBER_OF_ROWS_IN_EXCEL_SHEET_SV) Integer maxNumberOfRows) {
        this.workbookWindow = workbookWindow;
        this.maxNumberOfRows = maxNumberOfRows;
    }

    public WorkbookManager create(){
        return new WorkbookManager(workbookWindow, maxNumberOfRows);
    }

}
