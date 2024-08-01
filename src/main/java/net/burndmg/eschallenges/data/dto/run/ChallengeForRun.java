package net.burndmg.eschallenges.data.dto.run;


import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;
import java.util.Map;

public record ChallengeForRun (

        Map<String, Object> indexSettings,
        String idealRequest,
        List<ChallengeTest> tests,
        boolean expectsTheSameOrder
) {}
