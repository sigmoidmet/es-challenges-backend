package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.data.dto.ParticipantChallengePage;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParticipantChallengesControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void participantChallenges_whenNoPageSettings_shouldReturnAllInTheSameOrder() {
        Challenge first = testIndexer.indexRandomChallengeAndReturnIt("1");
        Challenge second = testIndexer.indexRandomChallengeAndReturnIt("2");

        webTestClient
                .get()
                .uri("/challenges")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.challenges.total").isEqualTo(2)
                .jsonPath("$.challenges.size").isEqualTo(2)
                .jsonPath("$.challenges.lastSortValue").exists()

                .jsonPath("$.challenges.result[0].id").isEqualTo(first.id())
                .jsonPath("$.challenges.result[0].title").isEqualTo(first.title())

                .jsonPath("$.challenges.result[1].id").isEqualTo(second.id())
                .jsonPath("$.challenges.result[1].title").isEqualTo(second.title());
    }

    @Test
    void participantChallenges_whenDesc_shouldReturnAllInTheOppositeOrder() {
        Challenge first = testIndexer.indexRandomChallengeAndReturnIt("1");
        Challenge second = testIndexer.indexRandomChallengeAndReturnIt("2");

        webTestClient
                .get()
                .uri("/challenges?direction=DESC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.challenges.total").isEqualTo(2)
                .jsonPath("$.challenges.size").isEqualTo(2)
                .jsonPath("$.challenges.lastSortValue").exists()

                .jsonPath("$.challenges.result[0].id").isEqualTo("2")
                .jsonPath("$.challenges.result[0].title").isEqualTo(second.title())

                .jsonPath("$.challenges.result[1].id").isEqualTo(first.id())
                .jsonPath("$.challenges.result[1].title").isEqualTo(first.title());
    }

    @Test
    void participantChallenges_whenPageSizeLesserThanAllResults_shouldReturnOnlyPageSize() {
        Challenge first = testIndexer.indexRandomChallengeAndReturnIt("1");

        testIndexer.indexRandomChallengeAndReturnIt("2");

        webTestClient
                .get()
                .uri("/challenges?size=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.challenges.total").isEqualTo(2)
                .jsonPath("$.challenges.size").isEqualTo(1)
                .jsonPath("$.challenges.lastSortValue").exists()

                .jsonPath("$.challenges.result[0].id").isEqualTo(first.id())
                .jsonPath("$.challenges.result[0].title").isEqualTo(first.title());
    }

    @Test
    void participantChallenges_whenTwoRequestsWithSize1_shouldReturnLastPageOnSecondRequest() {
        testIndexer.indexRandomChallengeAndReturnIt("1");
        Challenge second = testIndexer.indexRandomChallengeAndReturnIt("2");

        ParticipantChallengePage firstResponseBody = webTestClient
                .get()
                .uri("/challenges?size=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ParticipantChallengePage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(firstResponseBody);

        webTestClient
                .get()
                .uri("/challenges?size=1&searchAfter=" + firstResponseBody.challenges().lastSortValue())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.challenges.total").isEqualTo(2)
                .jsonPath("$.challenges.size").isEqualTo(1)
                .jsonPath("$.challenges.lastSortValue").exists()

                .jsonPath("$.challenges.result[0].id").isEqualTo(second.id())
                .jsonPath("$.challenges.result[0].title").isEqualTo(second.title());
    }

    @Test
    void participantChallenges_whenGotToTheEnd_shouldReturnEmptyResult() {
        testIndexer.indexRandomChallengeAndReturnIt("1");
        testIndexer.indexRandomChallengeAndReturnIt("2");

        ParticipantChallengePage firstResponseBody = webTestClient
                .get()
                .uri("/challenges")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ParticipantChallengePage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(firstResponseBody);

        webTestClient
                .get()
                .uri("/challenges?size=1&searchAfter=" + firstResponseBody.challenges().lastSortValue())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.challenges.total").isEqualTo(2)
                .jsonPath("$.challenges.size").isEqualTo(0)
                .jsonPath("$.challenges.result").isEmpty();
    }

    @Test
    void challengeById_whenIdExists_shouldReturnChallenge() {
        Challenge challenge = testIndexer.indexRandomChallengeAndReturnIt("1");

        webTestClient
                .get()
                .uri("/challenges/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(challenge.id())
                .jsonPath("$.title").isEqualTo(challenge.title())
                .jsonPath("$.description").isEqualTo(challenge.description());
    }

    @Test
    void challengeById_whenIdNotExists_shouldReturn404() {
        testIndexer.indexRandomChallengeAndReturnIt("1");

        webTestClient
                .get()
                .uri("/challenges/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404);
    }
}