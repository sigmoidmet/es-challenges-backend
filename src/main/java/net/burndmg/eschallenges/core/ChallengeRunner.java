package net.burndmg.eschallenges.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunConfiguration;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunResult;
import net.burndmg.eschallenges.infrastructure.expection.instance.ConcurrentChallengeRunException;
import net.burndmg.eschallenges.repository.ChallengeRunRepository;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeRunner {

    private final ChallengeRunRepository challengeRunRepository;

    public ChallengeRunResult run(ChallengeRunConfiguration configuration) {
        String indexName = configuration.indexName();

        createAndValidateIndex(indexName, configuration.indexSettings());

        try {
            challengeRunRepository.saveAll(indexName, configuration.indexedData());
            return ChallengeRunResult
                    .builder()
                    .expectedResult(challengeRunRepository.search(indexName, configuration.idealRequest()))
                    .actualResult(challengeRunRepository.search(indexName, configuration.userRequest()))
                    .build();
        } catch (NoSuchIndexException e) {
            log.warn("The index {} was deleted during the challenge running, Retrying...", indexName);
            return run(configuration);
        } finally {
            challengeRunRepository.tryDeleteIndex(indexName);
        }
    }

    private void createAndValidateIndex(String indexName, Map<String, Object> indexSettings) {
        try {
            challengeRunRepository.tryCreateIndex(indexName, indexSettings);
        } catch (UncategorizedElasticsearchException e) {
            if (e.getMessage().contains("resource_already_exists_exception")) {
                throw new ConcurrentChallengeRunException("You can't run more than one challenge simultaneously.");
            }
            throw e;
        }

    }
}
