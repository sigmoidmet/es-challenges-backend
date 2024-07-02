package net.burndmg.eschallenges.data.model;

public record ChallengeAcceptanceFailedTest (

        String dataJson,
        String expectedOutput,
        String userOutput
) {}
