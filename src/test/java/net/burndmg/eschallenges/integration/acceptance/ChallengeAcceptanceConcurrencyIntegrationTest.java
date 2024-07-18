package net.burndmg.eschallenges.integration.acceptance;

import lombok.SneakyThrows;
import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeAcceptanceConcurrencyIntegrationTest extends IntegrationTestBase {

    // TODO: can flicker potentially, for now it hasn't happened but if it will we may use some RetryingTest to minimize flickering
    @Test
    @WithMockUser
    @SneakyThrows
    void tryRun_whenTwoSimultaneousRequestsByTheSameUser_shouldFailOne() {
        String request = """
                 {
                     "query": {
                       "term": {
                         "name.keyword": {
                           "value": "Nikola"
                         }
                       }
                     }
                 }
                """;
        String jsonIndexedDataArray = """
                                      [
                                          { "name": "Nikola" },
                                          { "name": "Andrijana" }
                                      ]
                                      """;
        TryRunRequest tryRunRequest = new TryRunRequest(jsonIndexedDataArray, request);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Nikola!")
                                                                  .idealRequest(request)
                                                                  .build());


        CompletableFuture<WebTestClient.ResponseSpec> future1 = CompletableFuture.supplyAsync(
                () -> post("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
        );
        CompletableFuture<WebTestClient.ResponseSpec> future2 = CompletableFuture.supplyAsync(
                () -> post("/challenges/" + challenge.id() + "/acceptances/try-run", tryRunRequest)
        );

        Set<Integer> expectedStatuses = new HashSet<>(Set.of(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.OK.value()));


        CompletableFuture.allOf(future1, future2).join();

        future1.get().expectStatus().value(expectedStatuses::remove);
        future2.get().expectStatus().value(expectedStatuses::remove);

        assertTrue(expectedStatuses.isEmpty());
    }

    // TODO: can flicker potentially, for now it hasn't happened but if it will we may use some RetryingTest to minimize flickering
    @Test
    @WithMockUser
    @SneakyThrows
    void run_whenTwoSimultaneousRequestsByTheSameUser_shouldFailOne() {
        String request = """
                 {
                     "query": {
                       "term": {
                         "name.keyword": {
                           "value": "Nikola"
                         }
                       }
                     }
                 }
                """;
        RunRequest runRequest = new RunRequest(request);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find Nikola!")
                                                                  .idealRequest(request)
                                                                  .jsonChallengeTestArray(
                                                                          """
                                                                            [
                                                                                { "name": "Minh" },
                                                                                { "name": "Khai" }
                                                                            ]
                                                                          """)
                                                                 .jsonChallengeTestArray(
                                                                            """ 
                                                                            [
                                                                                { "name": "Nikola" },
                                                                                { "name": "Andrijana" }
                                                                            ]
                                                                            """)
                                                                  .build());


        CompletableFuture<WebTestClient.ResponseSpec> future1 = CompletableFuture.supplyAsync(
                () -> post("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
        );
        CompletableFuture<WebTestClient.ResponseSpec> future2 = CompletableFuture.supplyAsync(
                () -> post("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
        );

        Set<Integer> expectedStatuses = new HashSet<>(Set.of(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.OK.value()));


        CompletableFuture.allOf(future1, future2).join();

        future1.get().expectStatus().value(expectedStatuses::remove);
        future2.get().expectStatus().value(expectedStatuses::remove);

        assertTrue(expectedStatuses.isEmpty());
    }
}
