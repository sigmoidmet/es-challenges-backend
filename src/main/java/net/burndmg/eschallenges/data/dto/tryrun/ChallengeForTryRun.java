package net.burndmg.eschallenges.data.dto.tryrun;

public record ChallengeForTryRun (
        String jsonIndexMappings,
        String idealRequest,
        boolean expectsTheSameOrder
) {}
