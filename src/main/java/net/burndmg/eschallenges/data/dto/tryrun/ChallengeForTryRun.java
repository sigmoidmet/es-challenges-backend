package net.burndmg.eschallenges.data.dto.tryrun;

import java.util.Map;

public record ChallengeForTryRun (
        Map<String, Object> indexSettings,
        String idealRequest,
        boolean ordered
) {}
