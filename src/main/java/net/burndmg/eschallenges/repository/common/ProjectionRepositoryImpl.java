package net.burndmg.eschallenges.repository.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProjectionRepositoryImpl implements ProjectionRepository {

    private final ReactiveElasticsearchOperations elasticsearchOperations;

    @Override
    public <T> Mono<T> findById(String id, Class<T> projectionType, IndexCoordinates indexCoordinates) {
        return elasticsearchOperations.get(id, projectionType, indexCoordinates);
    }
}
