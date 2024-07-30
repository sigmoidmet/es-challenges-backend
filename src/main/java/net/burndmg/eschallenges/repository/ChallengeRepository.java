package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.repository.common.IndexRepository;
import net.burndmg.eschallenges.repository.common.PaginationRepository;
import net.burndmg.eschallenges.repository.common.ProjectionRepository;
import net.burndmg.eschallenges.repository.common.SavingRepository;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

public interface ChallengeRepository extends Repository<Challenge, String>,
                                             PaginationRepository<Challenge>,
                                             ProjectionRepository,
                                             SavingRepository<Challenge>,
                                             IndexRepository {

    IndexCoordinates UPDATE_INDEX = IndexCoordinates.of(Challenge.UPDATE_INDEX_NAME);
    IndexCoordinates READ_INDEX = IndexCoordinates.of(Challenge.READ_INDEX_NAME);

    default Mono<Boolean> isReadAndUpdateAliasesPointToDifferentIndices() {
        return findAliases(UPDATE_INDEX)
                .map(indices -> !indices.contains(READ_INDEX.getIndexName()));
    }

    default Mono<Challenge> saveToReadIndex(Challenge challenge) {
        return save(challenge, READ_INDEX);
    }

    default Mono<Challenge> saveToUpdateIndex(Challenge challenge) {
        return save(challenge, UPDATE_INDEX);
    }

    default <T> Mono<T> findById(String id, Class<T> projectionType) {
        return findById(id, projectionType, READ_INDEX);
    }

    default <T> Mono<Page<T>> findAllAfter(PageSettings pageSettings, Class<T> projectionType) {
        return findAllAfter(pageSettings, projectionType, READ_INDEX);
    }
}
