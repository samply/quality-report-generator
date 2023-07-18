package de.samply.reporter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloneUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T extends Object> T clone(T object) throws CloneUtilsException {
        try {
            return cloneWithoutExceptionHandling(object);
        } catch (JsonProcessingException e) {
            throw new CloneUtilsException(e);
        }

    }

    private static <T extends Object> T cloneWithoutExceptionHandling(T object) throws JsonProcessingException {
        String serializedObject = objectMapper.writeValueAsString(object);
        return (T) objectMapper.readValue(serializedObject, object.getClass());
    }

}
