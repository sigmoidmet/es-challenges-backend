package net.burndmg.eschallenges.repository.common;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.TimestampBasedSortable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveSearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;

import static net.burndmg.eschallenges.data.model.TimestampBasedSortable.TIMESTAMP_FIELD;
import static net.burndmg.eschallenges.infrastructure.util.RepositoryProjectionUtil.queryBuilderWithProjectionFor;
import static org.springframework.util.CollectionUtils.isEmpty;


@RequiredArgsConstructor
public class PaginationRepositoryImpl<ENTITY extends TimestampBasedSortable> implements PaginationRepository<ENTITY> {

    private final ReactiveElasticsearchOperations elasticsearchOperations;

    @Override
    public <T> Mono<Page<T>> findAllAfter(PageSettings pageSettings, Class<T> projectionType, IndexCoordinates indexCoordinates) {
        Sort sortByTimestamp = Sort.by(TIMESTAMP_FIELD);

        if (pageSettings.direction().isDescending()) {
            sortByTimestamp = sortByTimestamp.descending();
        }

        Query query = queryBuilderWithProjectionFor(projectionType)
                                 .withQuery(Query.findAll())
                                 .withMaxResults(pageSettings.size())
                                 .withSort(sortByTimestamp)
                                 .withSearchAfter(pageSettings.searchAfter() == null ?
                                                          List.of() :
                                                          List.of(pageSettings.searchAfter()))
                                 .build();

        return elasticsearchOperations
                .searchForHits(query, projectionType, indexCoordinates)
                .map(PaginationRepositoryImpl::toPage);
    }

    private static <T> Page<T> toPage(ReactiveSearchHits<T> result) {
        List<SearchHit<T>> hits = result.getSearchHits()
                                        .collectList()
                                        .block();

        if (hits == null) {
            return null;
        }

        return Page.<T>builder()
                   .result(hits.stream().map(SearchHit::getContent).toList())
                   .size(hits.size())
                   .total(result.getTotalHits())
                   .lastSortValue(toLastSortValue(hits))
                   .build();
    }

    private static <T> Long toLastSortValue(@Nullable List<SearchHit<T>> hits) {
        if (isEmpty(hits)) {
            return null;
        }

        return (Long) hits.get(hits.size() - 1)
                          .getSortValues()
                          .get(0);
    }
}
