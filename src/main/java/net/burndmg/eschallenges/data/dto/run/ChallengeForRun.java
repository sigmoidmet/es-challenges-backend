package net.burndmg.eschallenges.data.dto.run;


import java.util.List;
import java.util.Map;

public record ChallengeForRun (

        Map<String, Object> indexSettings,
        String idealRequest,
        List<String> jsonChallengeTestArrays,
        boolean ordered
) {}
