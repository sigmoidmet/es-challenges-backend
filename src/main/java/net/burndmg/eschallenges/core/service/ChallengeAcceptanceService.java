package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.ChallengeRunner;
import net.burndmg.eschallenges.data.dto.run.*;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeForTryRun;
import net.burndmg.eschallenges.data.dto.tryrun.ChallengeTryRunData;
import net.burndmg.eschallenges.data.dto.tryrun.TryRunResponse;
import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import net.burndmg.eschallenges.data.model.ChallengeAcceptanceFailedTest;
import net.burndmg.eschallenges.data.model.ChallengeTest;
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
                .map(challenge -> toRunTest(challengeTryRunData, challenge))
                .flatMap(this::runAndValidateResults)
                .map(runResult -> TryRunResponse.builder()
                                                .actualResponse(runResult.actualResult())
                                                .expectedResponse(runResult.expectedResult())
                                                .isSuccessful(runResult.isSuccessful())
                                                .build());
    }

    private RunTest toRunTest(ChallengeTryRunData challengeTryRunData, ChallengeForTryRun challenge) {
        Map<String, Object> indexSettings = objectMapper.fromJson(challenge.jsonIndexMappings());
        return RunTest.builder()
                      .username(challengeTryRunData.username())
                      .indexMappings(indexSettings)
                      .expectedResult(Mono.defer(() -> run(challengeTryRunData.username(),
                                                           indexSettings,
                                                           objectMapper.fromJsonList(challengeTryRunData.jsonIndexedDataArray()),
                                                           challenge.idealRequest())))
                      .jsonTestArray(objectMapper.fromJsonList(challengeTryRunData.jsonIndexedDataArray()))
                      .userRequest(challengeTryRunData.request())
                      .resultShouldBeOrdered(challenge.expectsTheSameOrder())
                      .build();
    }

    public Mono<ChallengeAcceptanceDto> runChallenge(ChallengeRunData runData) {
        return getChallengeById(runData.challengeId(), ChallengeForRun.class)
                .flatMapIterable(challenge -> challenge.tests()
                                                       .stream()
                                                       .map(test -> toRunTest(runData, challenge, test))
                                                       .toList())
                .concatMap(this::runAndValidateResults)
                .collectList()
                .flatMap(runResults -> getFirstFailedOrAny(runData, runResults));
    }

    private <T> Mono<T> getChallengeById(String id, Class<T> type) {
        return challengeRepository.findById(id, type)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }

    private RunTest toRunTest(ChallengeRunData runData, ChallengeForRun challenge, ChallengeTest test) {
        return RunTest.builder()
                      .username(runData.username())
                      .indexMappings(challenge.indexSettings())
                      .expectedResult(Mono.just(objectMapper.fromJsonList(test.jsonExpectedResultArray())))
                      .jsonTestArray(objectMapper.fromJsonList(test.jsonTestArray()))
                      .userRequest(runData.request())
                      .resultShouldBeOrdered(challenge.expectsTheSameOrder())
                      .build();
    }

    private Mono<ChallengeRunResult> runAndValidateResults(RunTest runTest) {
        return run(runTest.username(), runTest.indexMappings(), runTest.jsonTestArray(), runTest.userRequest())
                .flatMap(actualResult -> runTest.expectedResult()
                                                .map(expectedResult -> withSuccessStatus(runTest,
                                                                                         expectedResult,
                                                                                         actualResult)));
    }

    private ChallengeRunResult withSuccessStatus(RunTest runTest,
                                                 List<Map<String, Object>> expectedResult,
                                                 List<Map<String, Object>> actualResult) {
        return ChallengeRunResult
                .builder()
                .actualResult(actualResult)
                .expectedResult(expectedResult)
                .runData(runTest.jsonTestArray())
                .isSuccessful(isSuccessful(expectedResult, actualResult, runTest.resultShouldBeOrdered()))
                .build();
    }

    private Mono<List<Map<String, Object>>> run(String username,
                                                Map<String, Object> indexSettings,
                                                List<Map<String, Object>> indexedData,
                                                String request) {
        return challengeRunner.run(
                ChallengeRunConfiguration.builder()
                                         .indexName(username)
                                         .indexMappings(indexSettings)
                                         .indexedData(indexedData)
                                         .request(request)
                                         .build()
        );
    }

    private boolean isSuccessful(List<Map<String, Object>> expectedResult,
                                 List<Map<String, Object>> actualResult,
                                 boolean resultShouldBeOrdered) {
        if (!resultShouldBeOrdered) {
            return CollectionUtils.isEqualCollection(expectedResult, actualResult);
        } else {
            return expectedResult.equals(actualResult);
        }
    }

    private Mono<ChallengeAcceptanceDto> getFirstFailedOrAny(ChallengeRunData runData, List<ChallengeRunResult> runResults) {
        return runResults.stream()
                         .filter(runResult -> !runResult.isSuccessful())
                         .findFirst()
                         .map(failedRunResult -> saveFailedAcceptance(runData, failedRunResult))
                         .orElse(saveSuccessfulAcceptance(runData));
    }

    private Mono<ChallengeAcceptanceDto> saveFailedAcceptance(ChallengeRunData runData,
                                                              ChallengeRunResult failedRunResult) {
        var failedTest = ChallengeAcceptanceFailedTest.builder()
                                                      .testDataJson(failedRunResult.runData())
                                                      .actualOutput(failedRunResult.actualResult())
                                                      .expectedOutput(failedRunResult.expectedResult())
                                                      .build();

        return challengeAcceptanceRepository
                .save(acceptanceOf(runData).successful(false)
                                           .failedTest(failedTest)
                                           .build())
                .map(challengeAcceptanceMapper::toDto);
    }

    private Mono<ChallengeAcceptanceDto> saveSuccessfulAcceptance(ChallengeRunData runData) {
        return challengeAcceptanceRepository
                .save(acceptanceOf(runData).successful(true)
                                           .build())
                .map(challengeAcceptanceMapper::toDto);
    }


    private ChallengeAcceptance.ChallengeAcceptanceBuilder acceptanceOf(ChallengeRunData runData) {
        return ChallengeAcceptance.builder()
                                  .challengeId(runData.challengeId())
                                  .request(runData.request())
                                  .username(runData.username());
    }
}
