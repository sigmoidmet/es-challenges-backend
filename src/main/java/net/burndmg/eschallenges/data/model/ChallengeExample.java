package net.burndmg.eschallenges.data.model;


import jakarta.validation.constraints.NotNull;

public record ChallengeExample (

        @NotNull
        String testDataJson,

        @NotNull
        String expectedResult,

        String explanation
) {}
