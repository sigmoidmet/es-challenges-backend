package net.burndmg.eschallenges.infrastructure.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T readValue(String json, TypeReference<T> typeReference) {
        return objectMapper.readValue(json, typeReference);
    }
}
