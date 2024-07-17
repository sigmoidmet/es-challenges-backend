package net.burndmg.eschallenges.repository.common;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

import static net.burndmg.eschallenges.infrastructure.util.RepositoryProjectionUtil.sourceIncludes;

@RequiredArgsConstructor
public class ProjectionRepositoryImpl implements ProjectionRepository {

    private final ReactiveElasticsearchClient elasticsearchClient;

    @Override
    public <T> Mono<T> findById(String id, Class<T> projectionType, IndexCoordinates indexCoordinates) {
        return elasticsearchClient
                .get(GetRequest.of(builder -> builder.sourceIncludes(sourceIncludes(projectionType))
                                                     .id(id)
                                                     .index(indexCoordinates.getIndexName())),
                     projectionType)
                .mapNotNull(GetResult::source);
    }
}
