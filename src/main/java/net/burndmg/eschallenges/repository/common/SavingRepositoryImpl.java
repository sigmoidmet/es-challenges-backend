package net.burndmg.eschallenges.repository.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SavingRepositoryImpl<T> implements SavingRepository<T> {

    private final ReactiveElasticsearchOperations elasticsearchOperations;

    @Override
    public Mono<T> save(T entity, IndexCoordinates indexCoordinates) {
        return elasticsearchOperations.save(entity, indexCoordinates);
    }
}
