package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public class ChallengeAcceptanceOrderingTest extends IntegrationTestBase {

    @Test
    void tryRun_whenUnordered_shouldBeTrueWithAResultInAnyOrder() {
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
                .jsonPath("$.isSuccessful").isEqualTo("true");
    }

    @Test
    void run_whenUnordered_shouldBeTrueWithAResultInAnyOrder() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "desc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
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
                                                                                 {}
                                                                                """)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(true);
    }
}
