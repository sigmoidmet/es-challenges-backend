package net.burndmg.eschallenges.integration;

import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

public class NotSecuredEndpointIntegrationTest extends IntegrationTestBase {

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenTryingToCallNotSecuredEndpoint_shouldBeRejected() {
        get("/not-secured-integration-test-endpoint").expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }
}
