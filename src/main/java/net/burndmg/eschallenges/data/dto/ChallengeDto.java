package net.burndmg.eschallenges.data.dto;

import lombok.Builder;
import lombok.Singular;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;

@Builder
public record ChallengeDto (
        String title,
        String description,
        String jsonIndexMappings,
        String idealRequest,

        @Singular
        List<ChallengeExample> examples,

        @Singular
        List<ChallengeTest> tests
) {}
