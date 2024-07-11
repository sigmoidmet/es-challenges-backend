package net.burndmg.eschallenges.integration.util;

import net.burndmg.eschallenges.data.model.ChallengeTest;

import java.util.List;
import java.util.Map;

public class TestUtil {

    @SafeVarargs
    public static ChallengeTest challengeTest(Map<String, Object>... tests) {
        return new ChallengeTest(List.of(tests));
    }
}
