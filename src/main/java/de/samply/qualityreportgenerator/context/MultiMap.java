package de.samply.qualityreportgenerator.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiMap {

  private Map<String, Object> rootMap = new HashMap<>();

  public void put(Object value, String... keys) {
    Map<String, Object> map = rootMap;
    if (keys.length > 0) {
      for (int i = 0; i < keys.length; i++) {
        Object temp = map.get(keys[i]);
        if (temp == null) {
          if (i + 1 < keys.length) {
            Map<String, Object> map2 = new HashMap<>();
            map.put(keys[i], map2);
            map = map2;
          } else {
            map.put(keys[i], value);
          }
        } else {
          if (temp instanceof Map<?, ?>) {
            map = (Map<String, Object>) temp;
          }
        }
      }

    }

  }

  public Object get(String... keys) {
    Object result = null;
    Map<String, Object> map = rootMap;
    for (String key : keys) {
      Object temp = map.get(key);
      if (temp != null) {
        if (temp instanceof Map<?, ?>) {
          map = (Map<String, Object>) temp;
        } else {
          result = temp;
        }
      } else {
        break;
      }
    }

    return result;
  }

  public List<Object> getAll(String... keys) {
    List<Object> result = new ArrayList<>();

    Map<String, Object> map = rootMap;
    for (String key : keys) {
      Object temp = map.get(key);
      if (temp != null) {
        if (temp instanceof Map<?, ?>) {
          map = (Map<String, Object>) temp;
        }
      } else {
        break;
      }
    }
    addAllValues(result, map);
    return result;
  }

  private void addAllValues(List<Object> result, Map<String, Object> map) {
    for (Object element : map.values()) {
      if (element instanceof Map<?, ?>) {
        addAllValues(result, (Map<String, Object>) element);
      } else {
        result.add(element);
      }
    }

  }

  public Set<String> getKeySet(String... keys) {
    Map<String, Object> map = rootMap;
    for (String key : keys) {
      Object temp = map.get(key);
      if (temp != null) {
        map = (temp instanceof Map<?, ?>) ? (Map<String, Object>) temp : new HashMap<>();
      } else {
        map = new HashMap<>();
        break;
      }
    }
    return map.keySet();
  }


}
