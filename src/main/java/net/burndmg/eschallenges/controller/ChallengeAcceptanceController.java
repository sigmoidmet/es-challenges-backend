package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeAcceptanceService;
import net.burndmg.eschallenges.data.dto.run.ChallengeAcceptanceDto;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunData;
import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeTryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import net.burndmg.eschallenges.infrastructure.annotation.AuthUsername;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("challenges/{id}/acceptances")
@RequiredArgsConstructor
public class ChallengeAcceptanceController {

    private final ChallengeAcceptanceService challengeAcceptanceService;

    @PostMapping("try-run")
    @PreAuthorize("isAuthenticated()")
    public Mono<TryRunResponse> tryRun(@PathVariable String id,
                                       @RequestBody TryRunRequest tryRunRequest,
                                       @AuthUsername String username) {
        return challengeAcceptanceService.tryRunChallenge(
                ChallengeTryRunData.builder()
                                   .challengeId(id)
                                   .jsonIndexedDataArray(tryRunRequest.jsonIndexedDataArray())
                                   .request(tryRunRequest.request())
                                   .username(username)
                                   .build()
        );
    }

    @PostMapping("run")
    @PreAuthorize("isAuthenticated()")
    public Mono<ChallengeAcceptanceDto> run(@PathVariable String id,
                                            @RequestBody RunRequest runRequest,
                                            @AuthUsername String username) {
        return challengeAcceptanceService.runChallenge(
                ChallengeRunData.builder()
                                .challengeId(id)
                                .request(runRequest.request())
                                .username(username)
                                .build()
        );
    }
}
