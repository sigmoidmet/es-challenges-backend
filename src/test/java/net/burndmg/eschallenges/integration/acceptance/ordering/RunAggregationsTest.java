package net.burndmg.eschallenges.integration.acceptance.ordering;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.data.model.RunSearchResponseJson;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public class RunAggregationsTest extends IntegrationTestBase {

    @Test
    void run_whenWrongAggregationQuery_shouldFail() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                   "size": 0,
                                                   "aggs": {
                                                        "Max": {
                                                            "min": { "field": "count" }
                                                        }
                                                  }
                                               }
                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Max!")
                                                                  .test(new ChallengeTest(
                                                                          """
                                                                            [
                                                                                { "name": "Max", "count": 100 },
                                                                                { "name": "Min", "count": 0 }
                                                                            ]
                                                                          """,
                                                                          new RunSearchResponseJson(
                                                                                  "[]",
                                                                                  """
                                                                                  {
                                                                                    "Max": {
                                                                                        "value": 100
                                                                                    }
                                                                                  }
                                                                                  """
                                                                          )
                                                                  ))
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(false);
    }
}
