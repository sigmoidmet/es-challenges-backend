package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.integration.util.TestUtil.withAllResult;

@WithMockUser
public class ChallengeAcceptanceOrderingTest extends IntegrationTestBase {

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
    void run_whenUnorderedWithResultInAnyOrder_shouldBeTrue() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "desc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                               [
                                                                                                   { "name": "Dmitry" },
                                                                                                   { "name": "Maria" }
                                                                                               ]
                                                                                             """))
                                                                  .test(withAllResult("""
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
                .jsonPath("$.successful").isEqualTo(true);
    }

    @Test
    void run_whenOrderedWithResultInTheSameOrder_shouldBeTrue() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "asc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """))
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "A1" },
                                                                                                { "name": "B2" },
                                                                                                { "name": "C3" },
                                                                                                { "name": "D4" },
                                                                                                { "name": "E5" }
                                                                                            ]
                                                                                          """))
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(true);
    }

    @Test
    void run_whenOrderedWithResultInAnotherOrder_shouldBeFalse() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "desc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """))
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "A1" },
                                                                                                { "name": "B2" },
                                                                                                { "name": "C3" },
                                                                                                { "name": "D4" },
                                                                                                { "name": "E5" }
                                                                                            ]
                                                                                          """))

                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(false);
    }
}
