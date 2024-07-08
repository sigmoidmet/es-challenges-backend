package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.ChallengeRunner;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunConfiguration;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunResult;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeForTryRun;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeAcceptanceService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeRunner challengeRunner;

    public TryRunResponse tryRun(TryRunData tryRunData) {
        ChallengeForTryRun challenge = getChallengeById(tryRunData.challengeId());

        ChallengeRunResult runResult = challengeRunner.run(
                ChallengeRunConfiguration.builder()
                                         .indexName(tryRunData.username())
                                         .indexSettings(challenge.indexSettings())
                                         .indexedData(tryRunData.indexedData())
                                         .idealRequest(challenge.idealRequest())
                                         .userRequest(tryRunData.request())
                                         .build()
        );

        return TryRunResponse.builder()
                             .actualResponse(runResult.actualResult())
                             .expectedResponse(runResult.expectedResult())
                             .isSuccessful(runResult.expectedResult().equals(runResult.actualResult()))
                             .build();
    }

    private ChallengeForTryRun getChallengeById(String id) {
        return challengeRepository.findById(id, ChallengeForTryRun.class)
                                  .orElseThrow(() -> new NotFoundException("There is no challenge by id " + id));
    }
}
