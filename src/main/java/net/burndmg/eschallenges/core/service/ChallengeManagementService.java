package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.ChallengeRunner;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeResponse;
import net.burndmg.eschallenges.data.dto.TestsUpdate;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunConfiguration;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.infrastructure.util.JsonUtil;
import net.burndmg.eschallenges.infrastructure.util.JsonsDiscriminator;
import net.burndmg.eschallenges.infrastructure.util.ObjectMapperWrapper;
import net.burndmg.eschallenges.map.ChallengeMapper;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChallengeManagementService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMapper challengeMapper;
    private final ChallengeRunner challengeRunner;
    private final ObjectMapperWrapper objectMapper;

    public Mono<SaveChallengeResponse> create(SaveChallengeDto challenge, String username) {
        return createTestsWithResults(challenge.jsonTestArrays(), challenge, username)
                .map(tests -> challengeMapper.toModel(challenge, tests))
                .flatMap(this::save);
    }

    public Mono<SaveChallengeResponse> update(String id, SaveChallengeDto updatingChallenge, String username) {
        return findById(id)
                .map(savedChallenge -> prepareTestsForUpdate(updatingChallenge, savedChallenge))
                .flatMap(testsUpdate -> runTestsAndMergeWithExisting(id, updatingChallenge, username, testsUpdate))
                .flatMap(this::save);
    }

    private TestsUpdate prepareTestsForUpdate(SaveChallengeDto updatingChallenge, ChallengeDto savedChallenge) {
        if (JsonUtil.equalsNormalizedJsons(updatingChallenge.idealRequest(), savedChallenge.idealRequest()) &&
            JsonUtil.equalsNormalizedJsons(updatingChallenge.jsonIndexMappings(), savedChallenge.jsonIndexMappings())) {
            return JsonsDiscriminator.<ChallengeTest>of(updatingChallenge.jsonTestArrays())
                                     .with(savedChallenge.tests(), ChallengeTest::jsonTestArray)
                                     .into(TestsUpdate::new);
        } else {
            return new TestsUpdate(List.of(), updatingChallenge.jsonTestArrays());
        }
    }

    private Mono<Challenge> runTestsAndMergeWithExisting(String id,
                                                         SaveChallengeDto challenge,
                                                         String username,
                                                         TestsUpdate testsUpdate) {
        return createTestsWithResults(testsUpdate.jsonTestArraysWithoutResults(), challenge, username)
                .map(runTests -> challengeMapper.toModel(id, challenge, merge(testsUpdate.existingTestsWithResults(),
                                                                              runTests)));
    }

    private Mono<List<ChallengeTest>> createTestsWithResults(Collection<String> jsonTestArrays,
                                                             SaveChallengeDto challenge,
                                                             String username) {
        return Flux.fromIterable(jsonTestArrays)
                   .concatMap(jsonTestArray -> runAndGetTestWithResult(jsonTestArray, challenge, username))
                   .collectList();
    }

    private Mono<ChallengeTest> runAndGetTestWithResult(String jsonTestArray,
                                                        SaveChallengeDto challenge,
                                                        String username) {
        return challengeRunner.run(ChallengeRunConfiguration.builder()
                                                            .indexName(username)
                                                            .indexedData(objectMapper.fromJsonList(jsonTestArray))
                                                            .indexMappings(objectMapper.fromJson(challenge.jsonIndexMappings()))
                                                            .request(challenge.idealRequest())
                                                            .build())
                              .map(result -> new ChallengeTest(jsonTestArray, objectMapper.writeValueAsString(result)));
    }

    private List<ChallengeTest> merge(Collection<ChallengeTest> existingTests, Collection<ChallengeTest> runTests) {
        return Stream.concat(existingTests.stream(), runTests.stream()).toList();
    }

    private Mono<SaveChallengeResponse> save(Challenge model) {
        return challengeRepository.saveToReadIndex(model)
                                  .flatMap(challengeRepository::saveToUpdateIndex)
                                  .map(challenge -> new SaveChallengeResponse(challenge.id(), challenge.tests()));
    }

    public Mono<ChallengeDto> findById(String id) {
        return challengeRepository.findById(id, ChallengeDto.class)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }
}
