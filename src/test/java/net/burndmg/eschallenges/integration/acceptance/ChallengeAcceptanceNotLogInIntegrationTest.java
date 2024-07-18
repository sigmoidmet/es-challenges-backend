package net.burndmg.eschallenges.integration.acceptance;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ChallengeAcceptanceNotLogInIntegrationTest extends IntegrationTestBase {

    @Test
    void tryRun_whenSentByNotLogInUser_shouldForbid() {
        RunRequest runRequest = new RunRequest("doesn't matter");
        testIndexer.indexRandomChallengeAndReturnIt("1");


        post("/challenges/1/acceptances/try-run", runRequest)
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void run_whenSentByNotLogInUser_shouldForbid() {
        RunRequest runRequest = new RunRequest("doesn't matter");
        testIndexer.indexRandomChallengeAndReturnIt("1");


        post("/challenges/1/acceptances/run", runRequest)
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }
}
