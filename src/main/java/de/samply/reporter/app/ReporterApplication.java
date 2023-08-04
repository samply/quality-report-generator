package de.samply.reporter.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"de.samply"})
public class ReporterApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReporterApplication.class, args);
  }

}
