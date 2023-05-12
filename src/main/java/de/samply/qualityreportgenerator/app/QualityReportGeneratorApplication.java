package de.samply.qualityreportgenerator.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"de.samply"})
public class QualityReportGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(QualityReportGeneratorApplication.class, args);
  }

}
