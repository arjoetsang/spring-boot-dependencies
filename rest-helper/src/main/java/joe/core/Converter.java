package joe.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Converter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonToObject(String jsonString, Class<T> valueType) throws Exception {
        return objectMapper.readValue(jsonString, valueType);
    }

    public static <T> T jsonToObject(String jsonString, TypeReference<T> typeReference) throws Exception {
        return objectMapper.readValue(jsonString, typeReference);
    }
}
