package de.samply.reporter.template;

import de.samply.reporter.app.ReporterConst;
import de.samply.reporter.utils.EnvironmentUtils;
import de.samply.reporter.utils.VariablesReplacer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@Disabled
@SpringBootTest
class ReportTemplateManagerTest {

    @Autowired
    private Environment environment;
    private final String directory = "../dktk-reporter/templates";

    @Test
    void getQualityReportTemplate() {
        EnvironmentUtils environmentUtils = new EnvironmentUtils(environment);
        VariablesReplacer variablesReplacer = new VariablesReplacer(environmentUtils, "report-{TIMESTAMP}",
                ReporterConst.DEFAULT_TIMESTAMP_FORMAT);
        ReportTemplateManager reportTemplateManager = new ReportTemplateManager(variablesReplacer, "custom-{TIMESTAMP}", directory);
        System.out.println("Testing...");
    }
}
