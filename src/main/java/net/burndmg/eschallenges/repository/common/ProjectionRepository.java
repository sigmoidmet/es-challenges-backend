package net.burndmg.eschallenges.repository.common;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

public interface ProjectionRepository {

    <T> Mono<T> findById(String id, Class<T> projectionType, IndexCoordinates indexCoordinates);
}
