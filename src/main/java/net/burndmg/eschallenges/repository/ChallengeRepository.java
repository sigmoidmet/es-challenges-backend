package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.repository.common.PaginationRepository;
import net.burndmg.eschallenges.repository.common.ProjectionRepository;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ChallengeRepository extends ElasticsearchRepository<Challenge, String>, PaginationRepository, ProjectionRepository {

    IndexCoordinates INDEX = IndexCoordinates.of(Challenge.INDEX_NAME);

    default <T> Optional<T> findById(String id, Class<T> projectionType) {
        return findById(id, projectionType, INDEX);
    }

    default <T> Page<T> findAllAfter(PageSettings pageSettings, Class<T> projectionType) {
        return findAllAfter(pageSettings, projectionType, INDEX);
    }
}
