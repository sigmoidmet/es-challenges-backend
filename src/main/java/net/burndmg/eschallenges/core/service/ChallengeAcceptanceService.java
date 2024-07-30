package net.burndmg.eschallenges.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.ChallengeRunner;
import net.burndmg.eschallenges.data.dto.run.*;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeForTryRun;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeTryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import net.burndmg.eschallenges.data.model.ChallengeAcceptanceFailedTest;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.infrastructure.util.ObjectMapperWrapper;
import net.burndmg.eschallenges.map.ChallengeAcceptanceMapper;
import net.burndmg.eschallenges.repository.ChallengeAcceptanceRepository;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeAcceptanceService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeAcceptanceRepository challengeAcceptanceRepository;
    private final ChallengeRunner challengeRunner;
    private final ChallengeAcceptanceMapper challengeAcceptanceMapper;
    private final ObjectMapperWrapper objectMapper;

    public Mono<TryRunResponse> tryRunChallenge(ChallengeTryRunData challengeTryRunData) {
        return getChallengeById(challengeTryRunData.challengeId(), ChallengeForTryRun.class)
                .map(challenge -> RunTest.builder()
                                         .username(challengeTryRunData.username())
                                         .indexSettings(challenge.indexSettings())
                                         .idealRequest(challenge.idealRequest())
                                         .jsonTestArray(challengeTryRunData.jsonIndexedDataArray())
                                         .userRequest(challengeTryRunData.request())
                                         .resultShouldBeOrdered(challenge.ordered())
                                         .build())
                .flatMap(this::run)
                .map(runResultHolder -> TryRunResponse.builder()
                                                      .actualResponse(runResultHolder.result().actualResult())
                                                      .expectedResponse(runResultHolder.result().expectedResult())
                                                      .isSuccessful(runResultHolder.isSuccessful())
                                                      .build());
    }

    public Mono<ChallengeAcceptanceDto> runChallenge(ChallengeRunData runData) {
        return getChallengeById(runData.challengeId(), ChallengeForRun.class)
                .flatMapIterable(challenge -> challenge.jsonChallengeTestArrays()
                                                       .stream()
                                                       .map(test -> RunTest.builder()
                                                                           .username(runData.username())
                                                                           .indexSettings(challenge.indexSettings())
                                                                           .idealRequest(challenge.idealRequest())
                                                                           .jsonTestArray(test)
                                                                           .userRequest(runData.request())
                                                                           .resultShouldBeOrdered(challenge.ordered())
                                                                           .build())
                                                       .toList())
                .concatMap(this::run)
                .collectList()
                .flatMap(runResults -> getFirstFailedOrAny(runData, runResults));
    }

    private <T> Mono<T> getChallengeById(String id, Class<T> type) {
        return challengeRepository.findById(id, type)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }

    private Mono<ChallengeRunResultHolder> run(RunTest runTest) {
        return challengeRunner.run(
                ChallengeRunConfiguration.builder()
                                         .indexName(runTest.username())
                                         .indexSettings(runTest.indexSettings())
                                         .indexedData(toJson(runTest.jsonTestArray()))
                                         .idealRequest(runTest.idealRequest())
                                         .userRequest(runTest.userRequest())
                                         .build()
        ).map(result -> ChallengeRunResultHolder.builder()
                                                .result(result)
                                                .isSuccessful(isSuccessful(result, runTest.resultShouldBeOrdered()))
                                                .build());
    }

    private List<Map<String, Object>> toJson(String jsonDataArray) {
        return objectMapper.readValue(jsonDataArray, new TypeReference<>() {});
    }
    
    private boolean isSuccessful(ChallengeRunResult runResult, boolean resultShouldBeOrdered) {
        if (!resultShouldBeOrdered) {
            return CollectionUtils.isEqualCollection(runResult.expectedResult(), runResult.actualResult());
        } else {
            return runResult.expectedResult().equals(runResult.actualResult());
        }
    }

    private Mono<ChallengeAcceptanceDto> getFirstFailedOrAny(ChallengeRunData runData, List<ChallengeRunResultHolder> runResults) {
        return runResults.stream()
                         .filter(ruNResult -> !ruNResult.isSuccessful())
                         .findFirst()
                         .map(failedRunResult -> saveFailedAcceptance(runData, failedRunResult.result()))
                         .orElse(saveSuccessfulAcceptance(runData));
    }

    private Mono<ChallengeAcceptanceDto> saveFailedAcceptance(ChallengeRunData runData,
                                                              ChallengeRunResult failedRunResult) {
        var failedTest = ChallengeAcceptanceFailedTest.builder()
                                                      .testDataJson(failedRunResult.indexedDataJson())
                                                      .actualOutput(failedRunResult.actualResult())
                                                      .expectedOutput(failedRunResult.expectedResult())
                                                      .build();

        return challengeAcceptanceRepository
                .save(acceptanceOf(runData).successful(false).failedTest(failedTest).build())
                .map(challengeAcceptanceMapper::toDto);
    }

    private Mono<ChallengeAcceptanceDto> saveSuccessfulAcceptance(ChallengeRunData runData) {
        return challengeAcceptanceRepository
                .save(acceptanceOf(runData).successful(true).build())
                .map(challengeAcceptanceMapper::toDto);
    }


    private ChallengeAcceptance.ChallengeAcceptanceBuilder acceptanceOf(ChallengeRunData runData) {
        return ChallengeAcceptance.builder()
                                  .challengeId(runData.challengeId())
                                  .request(runData.request())
                                  .username(runData.username());
    }
}
