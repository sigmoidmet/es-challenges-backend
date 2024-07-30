package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

import java.util.Map;

@Builder
public record RunTest(
        String username,
        Map<String, Object> indexSettings,
        String idealRequest,
        String jsonTestArray,
        String userRequest,
        boolean resultShouldBeOrdered
) {}
