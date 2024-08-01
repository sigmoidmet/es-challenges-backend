package net.burndmg.eschallenges.data.dto;

import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;

public record TestsUpdate (
        List<ChallengeTest> existingTestsWithResults,
        List<String> jsonTestArraysWithoutResults
) {}
