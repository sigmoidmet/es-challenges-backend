package net.burndmg.eschallenges.repository.common;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface IndexRepository {

    Mono<Set<String>> findAliases(IndexCoordinates indexCoordinates);
}
