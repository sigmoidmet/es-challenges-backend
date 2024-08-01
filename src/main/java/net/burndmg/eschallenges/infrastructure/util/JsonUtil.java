package net.burndmg.eschallenges.infrastructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.Objects;

public class JsonUtil {

    // We don't want to depend on any object mapper settings,
    // so we wouldn't keep our own to remove any redundant whitespaces
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean equalsNormalizedJsons(String json1, String json2) {
        return Objects.equals(normalizeJson(json1), normalizeJson(json2));
    }

    @Nullable
    public static String normalizeJson(String json) {
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(objectMapper.readTree(json));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
