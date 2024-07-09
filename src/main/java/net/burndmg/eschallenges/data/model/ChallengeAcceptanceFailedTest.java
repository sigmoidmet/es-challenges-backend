package net.burndmg.eschallenges.data.model;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeAcceptanceFailedTest (

        List<Map<String, Object>> testDataJson,
        List<Map<String, Object>> expectedOutput,
        List<Map<String, Object>> actualOutput
) {}
