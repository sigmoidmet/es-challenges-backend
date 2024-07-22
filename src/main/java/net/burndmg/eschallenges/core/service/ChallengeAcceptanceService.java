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
                .flatMap(challenge -> challengeRunner.run(
                        ChallengeRunConfiguration.builder()
                                                 .indexName(challengeTryRunData.username())
                                                 .indexSettings(challenge.indexSettings())
                                                 .indexedData(toJson(challengeTryRunData.jsonIndexedDataArray()))
                                                 .idealRequest(challenge.idealRequest())
                                                 .userRequest(challengeTryRunData.request())
                                                 .build()
                ))
                .map(runResult -> TryRunResponse.builder()
                                                .actualResponse(runResult.actualResult())
                                                .expectedResponse(runResult.expectedResult())
                                                .isSuccessful(isSuccessful(runResult))
                                                .build());
    }

    public Mono<ChallengeAcceptanceDto> runChallenge(ChallengeRunData runData) {
        return getChallengeById(runData.challengeId(), ChallengeForRun.class)
                .flatMapIterable(challenge -> challenge.jsonChallengeTestArrays()
                                                       .stream()
                                                       .map(test -> RunTest.builder()
                                                                           .idealRequest(challenge.idealRequest())
                                                                           .indexSettings(challenge.indexSettings())
                                                                           .jsonTestArray(test)
                                                                           .build())
                                                       .toList())
                .concatMap(runTest -> challengeRunner.run(
                        ChallengeRunConfiguration.builder()
                                                 .indexName(runData.username())
                                                 .indexSettings(runTest.indexSettings())
                                                 .indexedData(toJson(runTest.jsonTestArray()))
                                                 .idealRequest(runTest.idealRequest())
                                                 .userRequest(runData.request())
                                                 .build()
                ))
                .takeWhile(this::isSuccessful)
                .last()
                .flatMap(runResult -> saveAcceptance(runData, runResult));
    }

    private List<Map<String, Object>> toJson(String jsonDataArray) {
        return objectMapper.readValue(jsonDataArray, new TypeReference<>() {});
    }

    private Mono<ChallengeAcceptanceDto> saveAcceptance(ChallengeRunData runData, ChallengeRunResult runResult) {
        boolean successful = isSuccessful(runResult);
        var failedTest = successful ?
                null :
                ChallengeAcceptanceFailedTest.builder()
                                             .testDataJson(runResult.indexedDataJson())
                                             .actualOutput(runResult.actualResult())
                                             .expectedOutput(runResult.expectedResult())
                                             .build();

        return challengeAcceptanceRepository.save(
                ChallengeAcceptance
                        .builder()
                        .challengeId(runData.challengeId())
                        .request(runData.request())
                        .failedTest(failedTest)
                        .username(runData.username())
                        .successful(successful)
                        .build()
        ).map(challengeAcceptanceMapper::toDto);
    }

    private <T> Mono<T> getChallengeById(String id, Class<T> type) {
        return challengeRepository.findById(id, type)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }
    
    private boolean isSuccessful(ChallengeRunResult runResult) {
        return CollectionUtils.isEqualCollection(runResult.expectedResult(), runResult.actualResult());
    }
}
