package net.burndmg.eschallenges.integration.util;

import net.burndmg.eschallenges.data.model.ChallengeTest;

public class TestUtil {

    public static ChallengeTest withAllResult(String testData) {
        return new ChallengeTest(testData, testData);
    }

    public static ChallengeTest withEmptyResult(String testData) {
        return new ChallengeTest(testData, "[]");
    }
}
