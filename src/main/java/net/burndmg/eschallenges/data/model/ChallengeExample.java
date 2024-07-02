package net.burndmg.eschallenges.data.model;


public record ChallengeExample (

    String testDataJson,
    String expectedResult,
    String explanation
) {}
