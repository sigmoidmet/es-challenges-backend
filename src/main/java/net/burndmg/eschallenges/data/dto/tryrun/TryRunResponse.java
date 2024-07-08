package net.burndmg.eschallenges.data.dto.tryrun;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record TryRunResponse (
        List<Map<String, Object>> expectedResponse,
        List<Map<String, Object>> actualResponse,
        boolean isSuccessful
) {}
