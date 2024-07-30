package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(username = "user")
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
        RunRequest runRequest = new RunRequest(request);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Dmitry!")
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Alyona" },
                                                                                                { "name": "Anna" },
                                                                                                { "name": "Maria" },
                                                                                                { "name": "Tatyana" },
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Alyona" },
                                                                                                { "name": "Anna" },
                                                                                                { "name": "Maria" },
                                                                                                { "name": "Tatyana" },
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Alexander" },
                                                                                                { "name": "Anna" },
                                                                                                { "name": "Maria" },
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Andrijana" },
                                                                                                { "name": "Milos" },
                                                                                                { "name": "Nikola" },
                                                                                                { "name": "Mina" },
                                                                                                { "name": "Petar" }
                                                                                            ]
                                                                                          """)
                                                                  .idealRequest(request)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("user")
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
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                { "name": "Andrijana" },
                                                                                                { "name": "Milos" },
                                                                                                { "name": "Nikola" },
                                                                                                { "name": "Mina" },
                                                                                                { "name": "Petar" }
                                                                                            ]
                                                                                          """)
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
                .jsonPath("$.username").isEqualTo("user")
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
                                                              "T3"
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
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                {
                                                                                                    "name": "Andrijana",
                                                                                                    "position": "intern"
                                                                                                },
                                                                                                {
                                                                                                    "name": "Nikola",
                                                                                                    "position": "intern"
                                                                                                },
                                                                                                {
                                                                                                    "name": "Dmitry",
                                                                                                    "position": "T3"
                                                                                                }
                                                                                            ]
                                                                                          """)
                                                                  .jsonChallengeTestArray("""
                                                                                            [
                                                                                                {
                                                                                                    "name": "Dmitry",
                                                                                                    "position": "T3"
                                                                                                }
                                                                                            ]
                                                                                          """)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("user")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(runRequest.request())
                .jsonPath("$.successful").isEqualTo(false)

                .jsonPath("$.failedTest.testDataJson[0]['name']").isEqualTo("Andrijana")
                .jsonPath("$.failedTest.testDataJson[0]['position']").isEqualTo("intern")
                .jsonPath("$.failedTest.testDataJson[1]['name']").isEqualTo("Nikola")
                .jsonPath("$.failedTest.testDataJson[1]['position']").isEqualTo("intern")
                .jsonPath("$.failedTest.testDataJson[2]['name']").isEqualTo("Dmitry")
                .jsonPath("$.failedTest.testDataJson[2]['position']").isEqualTo("T3")

                .jsonPath("$.failedTest.expectedOutput[0]['name']").isEqualTo("Andrijana")
                .jsonPath("$.failedTest.expectedOutput[0]['position']").isEqualTo("intern")
                .jsonPath("$.failedTest.expectedOutput[1]['name']").isEqualTo("Nikola")
                .jsonPath("$.failedTest.expectedOutput[1]['position']").isEqualTo("intern")

                .jsonPath("$.failedTest.actualOutput[0]['name']").isEqualTo("Dmitry")
                .jsonPath("$.failedTest.actualOutput[0]['position']").isEqualTo("T3");
    }
}
