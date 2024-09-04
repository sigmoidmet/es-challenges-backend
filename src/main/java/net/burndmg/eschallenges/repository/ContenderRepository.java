package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.model.Contender;
import net.burndmg.eschallenges.repository.common.ProjectionRepository;
import net.burndmg.eschallenges.repository.common.SavingRepository;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

public interface ContenderRepository extends Repository<Contender, String>,
                                             SavingRepository<Contender>,
                                             ProjectionRepository {

    IndexCoordinates UPDATE_INDEX = IndexCoordinates.of(Contender.UPDATE_INDEX_NAME);
    IndexCoordinates READ_INDEX = IndexCoordinates.of(Contender.READ_INDEX_NAME);

    default Mono<Contender> saveToReadIndex(Contender challenge) {
        return save(challenge, READ_INDEX);
    }

    default Mono<Contender> saveToUpdateIndex(Contender challenge) {
        return save(challenge, UPDATE_INDEX);
    }

    default <T> Mono<T> findById(String id, Class<T> projectionType) {
        return findById(id, projectionType, READ_INDEX);
    }
}
