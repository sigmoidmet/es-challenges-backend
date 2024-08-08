package net.burndmg.eschallenges.integration.management;

import net.burndmg.eschallenges.data.dto.SaveChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeResponse;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_MANAGEMENT_PRIVILEGE;
import static net.burndmg.eschallenges.integration.util.TestUtil.withAllResult;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateChallengeIntegrationTest extends IntegrationTestBase {

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void createChallenge() {
        SaveChallengeDto challenge = SaveChallengeDto.builder()
                                                     .title("Dream challenge")
                                                     .description("You've never dreamt of it actually")
                                                     .jsonTestArray("[{\"dream\":\"NIGHTMARE!!\"}]")
                                                     .example(new ChallengeExample("dream = nightmare",
                                                                                   "yes",
                                                                                   "it's a dream"))
                                                     .jsonIndexMappings("{}")
                                                     .idealRequest("{}")
                                                     .build();

        ChallengeTest expectedGeneratedTest = withAllResult(challenge.jsonTestArrays().get(0));

        var createResponse = postSuccessful("/challenges", challenge, SaveChallengeResponse.class);

        assertEquals(1, createResponse.testsWithResults().size());
        assertEquals(expectedGeneratedTest, createResponse.testsWithResults().get(0));

        getSuccessful("/challenges/" + createResponse.id())
                .jsonPath("$.title").isEqualTo(challenge.title())
                .jsonPath("$.tests[0].jsonTestArray").isEqualTo(expectedGeneratedTest.jsonTestArray())
                .jsonPath("$.tests[0].expectedResult.hitsJsonArray").isEqualTo(expectedGeneratedTest.expectedResult().hitsJsonArray())
                .jsonPath("$.idealRequest").isEqualTo(challenge.idealRequest())
                .jsonPath("$.examples[0].testDataJson").isEqualTo(challenge.examples().get(0).testDataJson())
                .jsonPath("$.examples[0].expectedResult").isEqualTo(challenge.examples().get(0).expectedResult())
                .jsonPath("$.examples[0].explanation").isEqualTo(challenge.examples().get(0).explanation())
                .jsonPath("$.jsonIndexMappings").isEqualTo(challenge.jsonIndexMappings())
                .jsonPath("$.description").isEqualTo(challenge.description());
    }

    @Test
    @WithMockUser(authorities = "I don't have any")
    void create_whenUserWithoutPrivilege_shouldForbid() {
        SaveChallengeDto challenge = SaveChallengeDto.builder()
                                                     .title("I will create my own challenge, haha!")
                                                     .description("They think, it's secure")
                                                     .jsonTestArray("\"bit\": \"it's not!!\"")
                                                     .example(new ChallengeExample("I am a hacker!",
                                                                                   "haha",
                                                                                   "haha haha"))
                                                     .jsonIndexMappings("\"will\":\"control\"")
                                                     .idealRequest("everything on this service!")
                                                     .build();

        post("/challenges", challenge).expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }
}
