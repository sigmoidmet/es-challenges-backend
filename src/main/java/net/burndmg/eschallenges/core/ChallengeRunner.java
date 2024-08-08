package net.burndmg.eschallenges.core;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.burndmg.eschallenges.data.dto.run.ChallengeRunConfiguration;
import net.burndmg.eschallenges.data.dto.run.RunSearchResponse;
import net.burndmg.eschallenges.infrastructure.expection.instance.ConcurrentChallengeRunException;
import net.burndmg.eschallenges.repository.run.ChallengeRunRepository;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeRunner {

    private final ChallengeRunRepository challengeRunRepository;

    public Mono<RunSearchResponse> run(ChallengeRunConfiguration configuration) {
        String indexName = configuration.indexName();

        return challengeRunRepository
                .createIndex(indexName, configuration.indexMappings())
                .onErrorMap(UncategorizedElasticsearchException.class, this::catchConcurrentChallengeRun)

                // We need to execute index creation -> data indexing -> searching in this particular order, so we're deferring these operations
                .then(Mono.defer(() -> challengeRunRepository.saveAll(indexName, configuration.indexedData())))
                .then(Mono.defer(() -> challengeRunRepository.search(indexName, configuration.request())))

                .doOnError(this::isIndexNotFound,
                           __ -> log.warn("The index {} was deleted during the challenge being running. Retrying...",
                                          indexName))
                .onErrorResume(this::isIndexNotFound, __ -> run(configuration))

                // We want to clean up this index in case of:
                // 1. a success(which means that we also need to do it before returning the value as we can get the subsequent run afterward)
                // 2. after any unhandled exceptions
                .onErrorResume(e -> challengeRunRepository.deleteIndex(indexName).then(Mono.error(e)))
                .flatMap(runResult -> challengeRunRepository.deleteIndex(indexName).thenReturn(runResult));
    }

    private RuntimeException catchConcurrentChallengeRun(UncategorizedElasticsearchException e) {
        if (e.getMessage().contains("resource_already_exists_exception")) {
            return new ConcurrentChallengeRunException("You can't run more than one challenge simultaneously.");
        }
        return e;
    }

    private boolean isIndexNotFound(Throwable e) {
       return e instanceof ElasticsearchException &&
              e.getMessage().contains("index_not_found_exception");
    }
}
