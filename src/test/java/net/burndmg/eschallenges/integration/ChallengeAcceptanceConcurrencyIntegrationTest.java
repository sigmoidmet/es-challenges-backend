package net.burndmg.eschallenges.integration;

import lombok.SneakyThrows;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeAcceptanceConcurrencyIntegrationTest extends IntegrationTestBase {

    // TODO: can flicker potentially, for now it hasn't happened but if it will we may use some RetryingTest to minimize flickering
    @Test
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
        List<Map<String, Object>> indexedData = List.of(
                Map.of("name", "Nikola"),
                Map.of("name", "Andrijana")
        );
        TryRunRequest tryRunRequest = new TryRunRequest(indexedData, request);

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
}
