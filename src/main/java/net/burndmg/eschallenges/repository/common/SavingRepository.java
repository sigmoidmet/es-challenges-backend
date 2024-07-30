package net.burndmg.eschallenges.repository.common;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

public interface SavingRepository<T> {

    Mono<T> save(T entity, IndexCoordinates indexCoordinates);
}
