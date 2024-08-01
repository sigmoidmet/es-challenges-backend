package net.burndmg.eschallenges.integration.management;

import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_MANAGEMENT_PRIVILEGE;

public class GetChallengeForManagementIntegrationTest extends IntegrationTestBase {

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void challengeById_whenIdExists_shouldReturnChallenge() {
        Challenge challenge = testIndexer.indexRandomChallengeAndReturnIt("1");

        getSuccessful("/challenges/1")
                .jsonPath("$.title").isEqualTo(challenge.title())
                .jsonPath("$.tests").exists()
                .jsonPath("$.idealRequest").isEqualTo(challenge.idealRequest())
                .jsonPath("$.examples").exists()
                .jsonPath("$.jsonIndexMappings").exists()
                .jsonPath("$.description").isEqualTo(challenge.description());
    }

    @Test
    @WithMockUser(authorities = "I don't have any")
    void challengeById_whenUserWithoutPrivilege_shouldForbid() {
        testIndexer.indexRandomChallengeAndReturnIt("1");

        get("/challenges/1").expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void challengeById_whenIdNotExists_shouldReturn404() {
        testIndexer.indexRandomChallengeAndReturnIt("1");

        get("/challenges/2").expectStatus().isEqualTo(404);
    }
}
