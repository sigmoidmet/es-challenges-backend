package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.integration.util.TestUtil.withEmptyResult;

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
                                                                  .test(withDmitryResult("""
                                                                                           [
                                                                                               { "name": "Dmitry" },
                                                                                               { "name": "Maria" }
                                                                                           ]
                                                                                         """))
                                                                  .test(withDmitryResult("""
                                                                                           [
                                                                                               { "name": "Alyona" },
                                                                                               { "name": "Anna" },
                                                                                               { "name": "Maria" },
                                                                                               { "name": "Tatyana" },
                                                                                               { "name": "Dmitry" },
                                                                                               { "name": "Maria" }
                                                                                           ]
                                                                                         """))
                                                                  .test(withDmitryResult("""
                                                                                           [
                                                                                               { "name": "Alyona" },
                                                                                               { "name": "Anna" },
                                                                                               { "name": "Maria" },
                                                                                               { "name": "Tatyana" },
                                                                                               { "name": "Dmitry" },
                                                                                               { "name": "Maria" }
                                                                                           ]
                                                                                         """))
                                                                  .test(withDmitryResult("""
                                                                                           [
                                                                                               { "name": "Alexander" },
                                                                                               { "name": "Anna" },
                                                                                               { "name": "Maria" },
                                                                                               { "name": "Dmitry" },
                                                                                               { "name": "Maria" }
                                                                                           ]
                                                                                         """))
                                                                  .test(withEmptyResult("""
                                                                                          [
                                                                                              { "name": "Andrijana" },
                                                                                              { "name": "Milos" },
                                                                                              { "name": "Nikola" },
                                                                                              { "name": "Mina" },
                                                                                              { "name": "Petar" }
                                                                                          ]
                                                                                        """))
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("user")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(request)
                .jsonPath("$.successful").isEqualTo(true)
                .jsonPath("$.failedTest").doesNotExist();
    }

    private ChallengeTest withDmitryResult(String jsonTestArray) {
        return withNameResult(jsonTestArray, "Dmitry");
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
                                                                  .test(withNameResult("""
                                                                                         [
                                                                                             { "name": "Dmitry" },
                                                                                             { "name": "Maria" }
                                                                                         ]
                                                                                       """, "Maria"))
                                                                  .test(withEmptyResult("""
                                                                                          [
                                                                                              { "name": "Andrijana" },
                                                                                              { "name": "Milos" },
                                                                                              { "name": "Nikola" },
                                                                                              { "name": "Mina" },
                                                                                              { "name": "Petar" }
                                                                                          ]
                                                                                        """))
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.id").exists()
                .jsonPath("$.username").isEqualTo("user")
                .jsonPath("$.challengeId").isEqualTo(challenge.id())
                .jsonPath("$.request").isEqualTo(runRequest.request())
                .jsonPath("$.successful").isEqualTo(true)
                .jsonPath("$.failedTest").doesNotExist();
    }

    private ChallengeTest withNameResult(String jsonTestArray, String nameValue) {
        return new ChallengeTest(
                jsonTestArray,
                String.format("""
                              [
                                  {
                                      "name": "%s"
                                  }
                              ]
                              """, nameValue)
        );
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
                                                                  .test(new ChallengeTest(
                                                                          """
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
                                                                          """,
                                                                          """
                                                                            [
                                                                                {
                                                                                    "name": "Andrijana",
                                                                                    "position": "intern"
                                                                                },
                                                                                {
                                                                                    "name": "Nikola",
                                                                                    "position": "intern"
                                                                                }
                                                                            ]
                                                                          """
                                                                  ))

                                                                  .test(withEmptyResult("""
                                                                                          [
                                                                                              {
                                                                                                  "name": "Dmitry",
                                                                                                  "position": "T3"
                                                                                              }
                                                                                          ]
                                                                                        """))
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
