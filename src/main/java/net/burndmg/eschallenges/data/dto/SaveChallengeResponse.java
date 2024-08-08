package net.burndmg.eschallenges.data.dto;

import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;

public record SaveChallengeResponse (
        String id,
        List<ChallengeTest> testsWithResults
) {}
