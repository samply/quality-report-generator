package de.samply.reporter.utils.multilevel;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;

public class MultiLevelComparable implements Comparable<MultiLevelComparable> {

  List<Comparable> comparableList = new ArrayList<>();

  public void addComparable(Comparable comparable) {
    comparableList.add(comparable);
  }

  public Comparable getComparable(int level) {
    return (comparableList.size() > level) ? comparableList.get(level) : null;
  }

  @Override
  public int compareTo(MultiLevelComparable o) {
    if (o == null) {
      return comparableList.isEmpty() ? 0 : 1;
    }
    if (comparableList.isEmpty()) {
      if (o.getComparable(0) != null) {
        return -1;
      }
    }
    for (int i = 0; i < comparableList.size(); i++) {
      Comparable comparable = comparableList.get(i);
      Comparable comparable2 = o.getComparable(i);
      if (comparable2 == null) {
        return 1;
      }
      int result =
          (isNumber(comparable) && isNumber(comparable2)) ? convertToInteger(comparable)
              .compareTo(convertToInteger(comparable2)) : comparable.compareTo(comparable2);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  private boolean isNumber(Comparable comparable) {
    return (comparable instanceof String) ? NumberUtils.isParsable((String) comparable) : false;
  }

  private Integer convertToInteger(Comparable comparable) {
    String number = (String) comparable;
    int index = number.indexOf(".");
    if (index < 0) {
      index = number.indexOf(",");
    }
    return Integer.valueOf((index > 0) ? number.substring(0, index) : number);
  }

}
