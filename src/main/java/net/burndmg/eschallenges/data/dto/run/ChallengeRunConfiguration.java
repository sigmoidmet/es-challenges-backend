package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeRunConfiguration (
        String indexName,
        Map<String, Object> indexSettings,
        List<Map<String, Object>> indexedData,
        String userRequest,
        String idealRequest
) {}
