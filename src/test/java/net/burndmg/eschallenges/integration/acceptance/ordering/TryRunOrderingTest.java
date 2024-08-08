package net.burndmg.eschallenges.integration.acceptance.ordering;

import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public class TryRunOrderingTest extends IntegrationTestBase {

    @Test
    void tryRun_whenUnorderedWithResultInAnyOrder_shouldBeTrue() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Dmitry", "age": 24 },
                                          { "name": "Maria", "age": 20 }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "_source": { "includes": [ "age", "name" ] },
                                                                                "sort": [ { "name.keyword": "desc" } ]
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .idealRequest("{}")
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.isSuccessful").isEqualTo(true);
    }

    @Test
    void tryRun_whenOrderedWithResultInTheSameOrder_shouldBeTrue() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "LinkedIn", "offers": 14 },
                                          { "name": "Glassdor", "age": 0 }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "sort": [ { "name.keyword": "asc" } ]
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .idealRequest("""
                                                                                {
                                                                                    "sort": [ { "name.keyword": "asc" } ]
                                                                                }
                                                                                """)
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.isSuccessful").isEqualTo(true);
    }

    @Test
    void tryRun_whenOrderedWithResultInAnotherOrder_shouldBeFalse() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "ChatGPT-4o", "goodAnswers": 100 },
                                          { "name": "AI Assistant", "goodAnswers": 1 }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "sort": [ { "name.keyword": "desc" } ]
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .idealRequest("""
                                                                                {
                                                                                    "sort": [ { "name.keyword": "asc" } ]
                                                                                }
                                                                                """)
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.isSuccessful").isEqualTo(false);
    }

    @Test
    void tryRun_whenAggregations_shouldNotDependOnOrder() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "ChatGPT-4o", "goodAnswers": 100 },
                                          { "name": "AI Assistant", "goodAnswers": 1 }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "sort": [ { "name.keyword": "asc" } ],
                                                                                "aggs": {
                                                                                    "good-answers-variants": {
                                                                                        "terms": {
                                                                                            "field": "goodAnswers"
                                                                                        }
                                                                                    },
                                                                                    "avg-answers": {
                                                                                        "avg": {
                                                                                            "field": "goodAnswers"
                                                                                        }
                                                                                    }
                                                                                }
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .idealRequest("""
                                                                              {
                                                                                "sort": [ { "name.keyword": "asc" } ],
                                                                                "aggs": {
                                                                                    "avg-answers": {
                                                                                        "avg": {
                                                                                            "field": "goodAnswers"
                                                                                        }
                                                                                    },
                                                                                    "good-answers-variants": {
                                                                                        "terms": {
                                                                                            "field": "goodAnswers"
                                                                                        }
                                                                                    }
                                                                                }
                                                                              }
                                                                              """)
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.isSuccessful").isEqualTo(true);
    }
}
