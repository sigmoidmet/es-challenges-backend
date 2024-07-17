package net.burndmg.eschallenges.repository.common;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.TimestampBasedSortable;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused") // It's used to ensure that our entities support this field
public interface PaginationRepository<ENTITY extends TimestampBasedSortable> {

    <T> Mono<Page<T>> findAllAfter(PageSettings pageSettings, Class<T> projectionType, IndexCoordinates indexCoordinates);
}
