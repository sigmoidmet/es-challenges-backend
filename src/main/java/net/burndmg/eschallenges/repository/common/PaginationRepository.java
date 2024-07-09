package net.burndmg.eschallenges.repository.common;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

public interface PaginationRepository {

    <T> Mono<Page<T>> findAllAfter(PageSettings pageSettings, Class<T> projectionType, IndexCoordinates indexCoordinates);
}
