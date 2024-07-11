package net.burndmg.eschallenges.data.dto;

import lombok.Builder;
import lombok.Singular;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;
import java.util.Map;

@Builder
public record ChallengeDto(
        String title,
        String description,
        Map<String, Object> indexSettings,
        String idealRequest,

        @Singular
        List<ChallengeExample> examples,

        @Singular
        List<ChallengeTest> challengeTests
) {}
