package net.burndmg.eschallenges.integration.util;

import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.data.model.RunSearchResponseJson;

public class TestUtil {

    public static ChallengeTest withAllResult(String testData) {
        return new ChallengeTest(testData, withoutAggregations(testData));
    }

    public static ChallengeTest withEmptyResult(String testData) {
        return new ChallengeTest(testData, withoutAggregations("[]"));
    }

    public static RunSearchResponseJson withoutAggregations(String testData) {
        return new RunSearchResponseJson(testData, null);
    }
}
