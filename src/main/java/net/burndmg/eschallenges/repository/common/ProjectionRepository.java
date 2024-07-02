package net.burndmg.eschallenges.repository.common;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Optional;

public interface ProjectionRepository {

    <T> Optional<T> findById(String id, Class<T> projectionType, IndexCoordinates indexCoordinates);
}
