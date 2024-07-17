package net.burndmg.eschallenges.data.dto;

import lombok.Builder;
import lombok.Singular;
import net.burndmg.eschallenges.data.model.ChallengeExample;

import java.util.List;

@Builder
public record ChallengeDto(
        String title,
        String description,
        String jsonIndexSettings,
        String idealRequest,

        @Singular
        List<ChallengeExample> examples,

        @Singular
        List<String> jsonChallengeTestArrays
) {}
