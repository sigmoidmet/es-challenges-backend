package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> indexedData = List.of(
                Map.of("name", expectedName),
                Map.of("name", "Maria")
        );
        TryRunRequest tryRunRequest = new TryRunRequest(indexedData, request);

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
        List<Map<String, Object>> indexedData = List.of(
                Map.of("name", "Dmitry"),
                Map.of("name", expectedName)
        );
        TryRunRequest tryRunRequest = new TryRunRequest(indexedData, """
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
        List<Map<String, Object>> indexedData = List.of(
                Map.of("name", "Andrijana"),
                Map.of("name", "Nikola")
        );
        TryRunRequest tryRunRequest = new TryRunRequest(indexedData, """
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
                .jsonPath("$.expectedResponse[0].name").isEqualTo("Dmitry")
                .jsonPath("$.actualResponse[0].name").isEqualTo("Maria")
                .jsonPath("$.isSuccessful").isEqualTo("false");
    }

    @Test
    void tryRun_whenSentInvalidRequest_shouldFail() {
        List<Map<String, Object>> indexedData = List.of(
                Map.of("name", "Nikola"),
                Map.of("name", "Andrijana")
        );
        TryRunRequest tryRunRequest = new TryRunRequest(indexedData, "{ DELETE * FROM Transactions;");

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
