package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeRunResult (
        RunSearchResponse actualResult,
        RunSearchResponse expectedResult,
        List<Map<String, Object>> runData,
        boolean isSuccessful
) {}
