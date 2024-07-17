package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

import java.util.Map;

@Builder
public record RunTest(
        Map<String, Object> indexSettings,
        String idealRequest,
        String jsonTestArray
) {}
