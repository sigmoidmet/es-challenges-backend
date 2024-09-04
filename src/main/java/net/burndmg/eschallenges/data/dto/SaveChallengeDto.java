package net.burndmg.eschallenges.data.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Singular;
import net.burndmg.eschallenges.data.model.ChallengeExample;

import java.util.List;

@Builder
public record SaveChallengeDto (

        @NotNull
        String title,

        @NotNull
        String description,

        @NotNull
        String jsonIndexMappings,

        @NotNull
        String idealRequest,

        @Singular
        List<ChallengeExample> examples,

        boolean expectsTheSameOrder,

        @NotEmpty
        @Singular
        List<String> jsonTestArrays
) {}
