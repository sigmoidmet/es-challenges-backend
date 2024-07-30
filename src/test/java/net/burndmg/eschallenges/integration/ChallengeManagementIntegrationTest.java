package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.dto.CreateChallengeResponse;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_CREATION_PRIVILEGE;


public class ChallengeManagementIntegrationTest extends IntegrationTestBase {


    @Test
    @WithMockUser(authorities = CHALLENGE_CREATION_PRIVILEGE)
    void createChallenge() {
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

        var savedId = postSuccessful("/challenges", challenge, CreateChallengeResponse.class);

        getSuccessful("/challenges/" + savedId.id())
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
    @WithMockUser(authorities = CHALLENGE_CREATION_PRIVILEGE)
    void updateChallenge() {
        Challenge indexedChallenge = testIndexer.indexRandomChallengeAndReturnIt("test");

        ChallengeDto updateChallenge = ChallengeDto.builder()
                                             .title("Some another title")
                                             .description(indexedChallenge.description())
                                             .jsonChallengeTestArrays(indexedChallenge.jsonChallengeTestArrays())
                                             .examples(indexedChallenge.examples())
                                             .jsonIndexSettings(indexedChallenge.jsonIndexSettings())
                                             .idealRequest("some another request")
                                             .build();

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id())
                .jsonPath("$.isDuringReindexingProcess").isEqualTo(false);

        getSuccessful("/challenges/" + indexedChallenge.id())
                .jsonPath("$.title").isEqualTo(updateChallenge.title())
                .jsonPath("$.jsonChallengeTestArrays[0]").isEqualTo(updateChallenge.jsonChallengeTestArrays().get(0))
                .jsonPath("$.idealRequest").isEqualTo(updateChallenge.idealRequest())
                .jsonPath("$.examples[0].testDataJson").isEqualTo(updateChallenge.examples().get(0).testDataJson())
                .jsonPath("$.examples[0].expectedResult").isEqualTo(updateChallenge.examples().get(0).expectedResult())
                .jsonPath("$.examples[0].explanation").isEqualTo(updateChallenge.examples().get(0).explanation())
                .jsonPath("$.jsonIndexSettings").isEqualTo(updateChallenge.jsonIndexSettings())
                .jsonPath("$.description").isEqualTo(updateChallenge.description());
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_CREATION_PRIVILEGE)
    void updateChallenge_whenAnotherUpdateAlias_shouldUpdateToAnotherIndex() {
        Challenge indexedChallenge = testIndexer.indexRandomChallengeAndReturnIt("test");

        ChallengeDto updateChallenge = ChallengeDto.builder()
                                                   .title("Some another title")
                                                   .description(indexedChallenge.description())
                                                   .jsonChallengeTestArrays(indexedChallenge.jsonChallengeTestArrays())
                                                   .examples(indexedChallenge.examples())
                                                   .jsonIndexSettings(indexedChallenge.jsonIndexSettings())
                                                   .idealRequest("some another request")
                                                   .build();

        testIndexer.switchUpdateAliasTo("another-index");

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id())
                .jsonPath("$.isDuringReindexingProcess").isEqualTo(true);
    }

    @Test
    @WithMockUser(authorities = "I don't have any")
    void save_whenUserWithoutPrivilege_shouldForbid() {
        ChallengeDto challenge = ChallengeDto.builder()
                                             .title("I will create my own challenge, haha!")
                                             .description("They think, it's secure")
                                             .jsonChallengeTestArray("\"but\": \"it's not!!\"")
                                             .example(new ChallengeExample("I am a hacker!",
                                                                           "haha",
                                                                           "haha haha"))
                                             .jsonIndexSettings("\"will\":\"control\"")
                                             .idealRequest("everything on this service!")
                                             .build();

        post("/challenges", challenge).expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_CREATION_PRIVILEGE)
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
    @WithMockUser(authorities = "I don't have any")
    void challengeById_whenUserWithoutPrivilege_shouldForbid() {
        testIndexer.indexRandomChallengeAndReturnIt("1");

        get("/challenges/1").expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_CREATION_PRIVILEGE)
    void challengeById_whenIdNotExists_shouldReturn404() {
        testIndexer.indexRandomChallengeAndReturnIt("1");

        get("/challenges/2").expectStatus().isEqualTo(404);
    }
}
