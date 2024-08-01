package net.burndmg.eschallenges.infrastructure.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper objectMapper;

    public Map<String, Object> fromJson(String jsonListMap) {
        return readValue(jsonListMap, new TypeReference<>() {});
    }

    public List<Map<String, Object>> fromJsonList(String jsonListMap) {
        return readValue(jsonListMap, new TypeReference<>() {});
    }

    @SneakyThrows
    public <T> T readValue(String json, TypeReference<T> typeReference) {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, typeReference);
    }

    @SneakyThrows
    public <T> String writeValueAsString(T value) {
        return objectMapper.writeValueAsString(value);
    }
}
