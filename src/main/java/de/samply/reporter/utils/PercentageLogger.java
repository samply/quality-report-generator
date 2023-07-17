package de.samply.reporter.utils;


import de.samply.reporter.logger.Logger;

public class PercentageLogger {

  private final Logger logger;
  private final int numberOfElements;
  private int counter = 0;
  private int lastPercentage = 0;

  public PercentageLogger(Logger logger, int numberOfElements, String description) {
    this.logger = logger;
    this.numberOfElements = numberOfElements;
    if (numberOfElements > 0) {
      logger.debug(description);
    }

  }

  public void incrementCounter() {
    if (this.numberOfElements > 0) {
      ++this.counter;
      double percentage = 100.0 * (double)this.counter / (double)this.numberOfElements;
      int ipercentage = (int) percentage;
      if (this.lastPercentage != ipercentage) {
        this.lastPercentage = ipercentage;
        if (ipercentage % 10 == 0) {
          this.logger.debug(ipercentage + " %");
        }
      }
    }
  }

}
