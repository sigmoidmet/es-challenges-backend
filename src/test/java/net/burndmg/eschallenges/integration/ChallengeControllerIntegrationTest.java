package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


public class ChallengeControllerIntegrationTest extends IntegrationTestBase {


    @Test
    void saveChallenge() {
        ChallengeDto challenge = ChallengeDto.builder()
                                             .title("Dream challenge")
                                             .description("You've never dreamt of it actually")
                                             .jsonChallengeTestArray("\"dream\": \"NIGHTMARE!!\"")
                                             .example(new ChallengeExample("dream = nightmare",
                                                                           "yes",
                                                                           "it's a dream"))
                                             .jsonIndexSettings("\"set\":\"get\"")
                                             .idealRequest("just return yes, haha")
                                             .build();

        String savedId = postSuccessful("/challenges", challenge, String.class);

        getSuccessful("/challenges/" + savedId)
                .jsonPath("$.title").isEqualTo(challenge.title())
                .jsonPath("$.jsonChallengeTestArrays[0]").isEqualTo(challenge.jsonChallengeTestArrays().get(0))
                .jsonPath("$.idealRequest").isEqualTo(challenge.idealRequest())
                .jsonPath("$.examples[0].testDataJson").isEqualTo(challenge.examples().get(0).testDataJson())
                .jsonPath("$.examples[0].expectedResult").isEqualTo(challenge.examples().get(0).expectedResult())
                .jsonPath("$.examples[0].explanation").isEqualTo(challenge.examples().get(0).explanation())
                .jsonPath("$.jsonIndexSettings").isEqualTo(challenge.jsonIndexSettings())
                .jsonPath("$.description").isEqualTo(challenge.description());
    }

    @Test
    void challengeById_whenIdExists_shouldReturnChallenge() {
        Challenge challenge = testIndexer.indexRandomChallengeAndReturnIt("1");

        getSuccessful("/challenges/1")
                .jsonPath("$.title").isEqualTo(challenge.title())
                .jsonPath("$.jsonChallengeTestArrays").exists()
                .jsonPath("$.idealRequest").isEqualTo(challenge.idealRequest())
                .jsonPath("$.examples").exists()
                .jsonPath("$.jsonIndexSettings").exists()
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
