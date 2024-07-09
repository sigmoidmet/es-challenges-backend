package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;
import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.Map;

@Builder
public record RunTest(
        Map<String, Object> indexSettings,
        String idealRequest,
        ChallengeTest test
) {}
