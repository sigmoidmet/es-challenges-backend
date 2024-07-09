package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class ChallengeAcceptanceRunIntegrationTest extends IntegrationTestBase {

    @Test
    void run_whenSentIdealRequest_shouldSucceed() {
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
        RunRequest runRequest = new RunRequest(request);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Dmitry!")
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", expectedName),
                                                                          Map.of("name", "Maria")
                                                                  )))
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Alyona"),
                                                                          Map.of("name", "Anna"),
                                                                          Map.of("name", "Maria"),
                                                                          Map.of("name", "Tatyana"),
                                                                          Map.of("name", expectedName),
                                                                          Map.of("name", "Maria")
                                                                  )))
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Alexander"),
                                                                          Map.of("name", "Anna"),
                                                                          Map.of("name", "Maria"),
                                                                          Map.of("name", expectedName),
                                                                          Map.of("name", "Maria")
                                                                  )))
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Andrijana"),
                                                                          Map.of("name", "Milos"),
                                                                          Map.of("name", "Nikola"),
                                                                          Map.of("name", "Mina"),
                                                                          Map.of("name", "Petar")
                                                                  )))
                                                                  .idealRequest(request)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("temporal_placeholder")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(request)
                .jsonPath("$.successful").isEqualTo(true)
                .jsonPath("$.failedTest").doesNotExist();
    }

    @Test
    void run_whenSentIdenticalRequest_shouldSucceed() {
        RunRequest runRequest = new RunRequest("""
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
                                                                  .title("Find Dmitry!")
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Dmitry"),
                                                                          Map.of("name", "Maria")
                                                                  )))
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Andrijana"),
                                                                          Map.of("name", "Milos"),
                                                                          Map.of("name", "Nikola"),
                                                                          Map.of("name", "Mina"),
                                                                          Map.of("name", "Petar")
                                                                  )))
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


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("temporal_placeholder")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(runRequest.request())
                .jsonPath("$.successful").isEqualTo(true)
                .jsonPath("$.failedTest").doesNotExist();
    }

    @Test
    void run_whenSentWrongRequest_shouldFail() {
        RunRequest runRequest = new RunRequest("""
                                                {
                                                  "query": {
                                                    "bool": {
                                                      "filter": [
                                                        {
                                                          "terms": {
                                                            "position.keyword": [
                                                              "intern",
                                                              "Not existing type like T1000"
                                                            ]
                                                          }
                                                        }
                                                      ]
                                                    }
                                                  }
                                                }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find interns!")
                                                                  .idealRequest("""
                                                                                  {
                                                                                      "query": {
                                                                                        "term": {
                                                                                          "position.keyword": {
                                                                                            "value": "intern"
                                                                                          }
                                                                                        }
                                                                                      }
                                                                                  }
                                                                                 """)
                                                                 .challengeTest(new ChallengeTest(List.of(
                                                                         Map.of("name", "Andrijana",
                                                                                "position", "intern"),
                                                                         Map.of("name", "Nikola",
                                                                                "position", "intern"),
                                                                         Map.of("name", "Dmitry",
                                                                                "position", "T3")
                                                                 )))
                                                                  .challengeTest(new ChallengeTest(List.of(
                                                                          Map.of("name", "Dmitry",
                                                                                 "position", "T3")
                                                                  )))
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("temporal_placeholder")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(runRequest.request())
                .jsonPath("$.successful").isEqualTo(true)
                .jsonPath("$.failedTest").doesNotExist();
    }
}
