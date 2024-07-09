package net.burndmg.eschallenges.data.model;

import java.util.List;
import java.util.Map;

public record ChallengeTest(
        List<Map<String, Object>> dataJson
) {}
