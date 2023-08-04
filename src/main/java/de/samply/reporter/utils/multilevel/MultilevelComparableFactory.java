package de.samply.reporter.utils.multilevel;

import java.util.Arrays;

public class MultilevelComparableFactory {

  public static MultiLevelComparable create (Comparable... comparables){
    MultiLevelComparable multiLevelComparable = new MultiLevelComparable();
    Arrays.stream(comparables).forEach(multiLevelComparable::addComparable);
    return multiLevelComparable;
  }

}
