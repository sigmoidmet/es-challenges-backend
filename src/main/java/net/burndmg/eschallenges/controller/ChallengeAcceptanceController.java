package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeAcceptanceService;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunRequest;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("challenges/{id}/acceptances")
@RequiredArgsConstructor
public class ChallengeAcceptanceController {

    private final ChallengeAcceptanceService challengeAcceptanceService;

    @PostMapping("try-run")
    public TryRunResponse tryRun(@PathVariable String id, @RequestBody TryRunRequest tryRunRequest) {
        // TODO: implement username properly once security would be enabled
        return challengeAcceptanceService.tryRun(
                TryRunData.builder()
                          .challengeId(id)
                          .indexedData(tryRunRequest.indexedData())
                          .request(tryRunRequest.request())
                          .username("temporal_placeholder")
                          .build()
        );
    }
}
