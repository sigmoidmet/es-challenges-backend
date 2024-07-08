package net.burndmg.eschallenges.data.dto.tryrun;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record TryRunData (
        String challengeId,
        List<Map<String, Object>> indexedData,
        String request,
        String username
) {}
