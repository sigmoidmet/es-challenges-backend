package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.burndmg.eschallenges.core.ChallengeRunner;
import net.burndmg.eschallenges.data.dto.run.*;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeForTryRun;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeTryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import net.burndmg.eschallenges.data.model.ChallengeAcceptanceFailedTest;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.map.ChallengeAcceptanceMapper;
import net.burndmg.eschallenges.repository.ChallengeAcceptanceRepository;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChallengeAcceptanceService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeAcceptanceRepository challengeAcceptanceRepository;
    private final ChallengeRunner challengeRunner;
    private final ChallengeAcceptanceMapper challengeAcceptanceMapper;

    public TryRunResponse tryRunChallenge(ChallengeTryRunData challengeTryRunData) {
        ChallengeForTryRun challenge = getChallengeById(challengeTryRunData.challengeId(), ChallengeForTryRun.class);

        ChallengeRunResult runResult = challengeRunner.run(
                ChallengeRunConfiguration.builder()
                                         .indexName(challengeTryRunData.username())
                                         .indexSettings(challenge.indexSettings())
                                         .indexedData(challengeTryRunData.indexedData())
                                         .idealRequest(challenge.idealRequest())
                                         .userRequest(challengeTryRunData.request())
                                         .build()
        );

        return TryRunResponse.builder()
                             .actualResponse(runResult.actualResult())
                             .expectedResponse(runResult.expectedResult())
                             .isSuccessful(runResult.expectedResult().equals(runResult.actualResult()))
                             .build();
    }

    public ChallengeAcceptanceDto runChallenge(ChallengeRunData runData) {
        ChallengeForRun challenge = getChallengeById(runData.challengeId(), ChallengeForRun.class);

        ChallengeRunResult lastRunResult = null;
        
        for (ChallengeTest challengeTest : challenge.challengeTests()) {
            lastRunResult = challengeRunner.run(
                    ChallengeRunConfiguration.builder()
                                             .indexName(runData.username())
                                             .indexSettings(challenge.indexSettings())
                                             .indexedData(challengeTest.dataJson())
                                             .idealRequest(challenge.idealRequest())
                                             .userRequest(runData.request())
                                             .build()
            );
            
            if (!isSuccessful(lastRunResult)) {
                break;
            }
        }
        
        


        return saveAcceptance(runData, lastRunResult);
    }
    
    private ChallengeAcceptanceDto saveAcceptance(ChallengeRunData runData, ChallengeRunResult runResult) {
        boolean successful = isSuccessful(runResult);
        var failedTest = successful ?
                null :
                ChallengeAcceptanceFailedTest.builder()
                                             .testDataJson(runResult.indexedDataJson())
                                             .actualOutput(runResult.actualResult())
                                             .expectedOutput(runResult.expectedResult())
                                             .build();

        return challengeAcceptanceMapper.toDto(challengeAcceptanceRepository.save(
                ChallengeAcceptance
                        .builder()
                        .challengeId(runData.challengeId())
                        .request(runData.request())
                        .failedTest(failedTest)
                        .username(runData.username())
                        .successful(successful)
                        .build()
        ));
        
    }

    @SneakyThrows
    private <T> T getChallengeById(String id, Class<T> type) {
        return challengeRepository.findById(id, type)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)))
                                  .toFuture()
                                  .get();
    }
    
    private boolean isSuccessful(ChallengeRunResult runResult) {
        return runResult.expectedResult().equals(runResult.actualResult());
    }
}
