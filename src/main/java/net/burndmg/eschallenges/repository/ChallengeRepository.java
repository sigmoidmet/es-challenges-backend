package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.repository.common.PaginationRepository;
import net.burndmg.eschallenges.repository.common.ProjectionRepository;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;

public interface ChallengeRepository extends ReactiveElasticsearchRepository<Challenge, String>, PaginationRepository, ProjectionRepository {

    IndexCoordinates INDEX = IndexCoordinates.of(Challenge.INDEX_NAME);

    default <T> Mono<T> findById(String id, Class<T> projectionType) {
        return findById(id, projectionType, INDEX);
    }

    default <T> Mono<Page<T>> findAllAfter(PageSettings pageSettings, Class<T> projectionType) {
        return findAllAfter(pageSettings, projectionType, INDEX);
    }
}
