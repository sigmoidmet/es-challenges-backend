package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public class ChallengeAcceptanceTryRunIntegrationTest extends IntegrationTestBase {

    @Test
    void tryRun_whenSentIdealRequest_shouldSucceed() {
        String request = """
                          {
                              "query": {
                                "term": {
                                  "name.keyword": {
                                    "value": "Dmitry"
                                  }
                                }
                              }
                          }
                         """;
        String expectedName = "Dmitry";
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Dmitry" },
                                          { "name": "Maria" }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, request);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Dmitry!")
                                                                  .idealRequest(request)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.expectedResponse[0].name").isEqualTo(expectedName)
                .jsonPath("$.actualResponse[0].name").isEqualTo(expectedName)
                .jsonPath("$.isSuccessful").isEqualTo("true");
    }

    @Test
    void tryRun_whenSentIdenticalRequest_shouldSucceed() {
        String expectedName = "Maria";
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Dmitry" },
                                          { "name": "Maria" }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "query": {
                                                                                  "bool": {
                                                                                    "filter": [
                                                                                      {
                                                                                        "terms": {
                                                                                          "name.keyword": [
                                                                                            "Maria",
                                                                                            "Not existing name"
                                                                                          ]
                                                                                        }
                                                                                      }
                                                                                    ]
                                                                                  }
                                                                                }
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Maria!")
                                                                  .idealRequest("""
                                                                                  {
                                                                                      "query": {
                                                                                        "term": {
                                                                                          "name.keyword": {
                                                                                            "value": "Maria"
                                                                                          }
                                                                                        }
                                                                                      }
                                                                                  }
                                                                                 """)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.expectedResponse[0].name").isEqualTo(expectedName)
                .jsonPath("$.actualResponse[0].name").isEqualTo(expectedName)
                .jsonPath("$.isSuccessful").isEqualTo("true");
    }

    @Test
    void tryRun_whenSentWrongRequest_shouldFail() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Andrijana" },
                                          { "name": "Nikola" }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, """
                                                                              {
                                                                                "query": {
                                                                                  "bool": {
                                                                                    "filter": [
                                                                                      {
                                                                                        "terms": {
                                                                                          "name.keyword": [
                                                                                            "Nikola",
                                                                                            "Not existing name"
                                                                                          ]
                                                                                        }
                                                                                      }
                                                                                    ]
                                                                                  }
                                                                                }
                                                                              }
                                                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Andrijana!")
                                                                  .idealRequest("""
                                                                                  {
                                                                                      "query": {
                                                                                        "term": {
                                                                                          "name.keyword": {
                                                                                            "value": "Andrijana"
                                                                                          }
                                                                                        }
                                                                                      }
                                                                                  }
                                                                                 """)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .jsonPath("$.expectedResponse[0].name").isEqualTo("Andrijana")
                .jsonPath("$.actualResponse[0].name").isEqualTo("Nikola")
                .jsonPath("$.isSuccessful").isEqualTo("false");
    }

    @Test
    void tryRun_whenSentInvalidRequest_shouldFail() {
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Nikola" },
                                          { "name": "Andrijana" }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, "{ DELETE * FROM Transactions;");

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Nikola!")
                                                                  .idealRequest("""
                                                                                  {
                                                                                      "query": {
                                                                                        "term": {
                                                                                          "name.keyword": {
                                                                                            "value": "Nikola"
                                                                                          }
                                                                                        }
                                                                                      }
                                                                                  }
                                                                                 """)
                                                                  .build());


        post("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
