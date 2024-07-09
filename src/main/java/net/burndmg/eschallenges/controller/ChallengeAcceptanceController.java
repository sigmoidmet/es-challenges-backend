package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeAcceptanceService;
import net.burndmg.eschallenges.data.dto.run.ChallengeAcceptanceDto;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunData;
import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeTryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("challenges/{id}/acceptances")
@RequiredArgsConstructor
public class ChallengeAcceptanceController {

    private final ChallengeAcceptanceService challengeAcceptanceService;

    @PostMapping("try-run")
    public Mono<TryRunResponse> tryRun(@PathVariable String id, @RequestBody TryRunRequest tryRunRequest) {
        // TODO: implement username properly once security would be enabled
        return challengeAcceptanceService.tryRunChallenge(
                ChallengeTryRunData.builder()
                                   .challengeId(id)
                                   .indexedData(tryRunRequest.indexedData())
                                   .request(tryRunRequest.request())
                                   .username("temporal_placeholder")
                                   .build()
        );
    }

    @PostMapping("run")
    public Mono<ChallengeAcceptanceDto> tryRun(@PathVariable String id, @RequestBody RunRequest runRequest) {
        // TODO: implement username properly once security would be enabled
        return challengeAcceptanceService.runChallenge(
                ChallengeRunData.builder()
                                .challengeId(id)
                                .request(runRequest.request())
                                .username("temporal_placeholder")
                                .build()
        );
    }
}
