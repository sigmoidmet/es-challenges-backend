package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import net.burndmg.eschallenges.data.dto.run.RunSearchResponse;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeAcceptanceFailedTest (

        List<Map<String, Object>> testDataJson,
        RunSearchResponse expectedOutput,
        RunSearchResponse actualOutput
) {}
