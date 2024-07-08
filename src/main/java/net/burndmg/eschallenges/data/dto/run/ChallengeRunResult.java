package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeRunResult(
        List<Map<String, Object>> expectedResult,
        List<Map<String, Object>> actualResult
) {}
