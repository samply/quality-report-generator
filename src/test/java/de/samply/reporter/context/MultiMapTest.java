package de.samply.reporter.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MultiMapTest {

  private String var1 = "var1";
  private String var2 = "var2";
  private String var3 = "var3";
  private String var4 = "var4";
  private String var5 = "var5";
  private String var6 = "var6";
  private String var7 = "var7";
  private String var8 = "var8";
  private String value1 = "value1";
  private String value2 = "value2";
  private String value3 = "value3";
  private String value4 = "value4";

  @Test
  void get() {
    MultiMap multiMap = new MultiMap();
    multiMap.put(value1, var1, var2, var3, var4);
    assertEquals(value1, multiMap.get(var1, var2, var3, var4));
    assertNotEquals(value1, multiMap.get(var1, var2, var3));
    assertNotEquals(value1, multiMap.get(var1, var2, var4, var3));
    assertNotEquals(value1, multiMap.get());

    multiMap.put(value2, var1, var2, var3, var5);
    multiMap.put(value3, var1, var2, var3, var6);
    multiMap.put(value4, var1, var2, var3, var7, var8);
    assertEquals(4, multiMap.getAll(var1).size());
    assertEquals(value1, multiMap.get(var1, var2, var3, var4));
    assertEquals(value2, multiMap.get(var1, var2, var3, var5));
    assertEquals(value3, multiMap.get(var1, var2, var3, var6));
    assertEquals(value4, multiMap.get(var1, var2, var3, var7, var8));
    String[] values = {value1, value2, value3, value4};
    containsAll(values, multiMap.getAll(var1));

    String[] variables = {var1};
    containsAll(variables, multiMap.getKeySet());

    String[] variables2 = {var2};
    containsAll(variables2, multiMap.getKeySet(var1));

    String[] variables3 = {var3};
    containsAll(variables3, multiMap.getKeySet(var1, var2));

    String[] variables4 = {var4, var5, var6, var7};
    containsAll(variables4, multiMap.getKeySet(var1, var2, var3));

    String[] variables5 = {};
    containsAll(variables5, multiMap.getKeySet(var1, var2, var3, var4));
  }

  private void containsAll(String[] values, Set<String> set){
    Arrays.stream(values).forEach(value -> assertTrue(set.contains(value)));
    assertEquals(values.length, set.size());
  }

  private void containsAll(String[] values, List<Object> list){
    Arrays.stream(values).forEach(value -> assertTrue(list.contains(value)));
    assertEquals(values.length, list.size());
  }

}
