package net.burndmg.eschallenges.repository.common;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;

import static net.burndmg.eschallenges.data.model.TimestampBasedSortable.TIMESTAMP_FIELD;
import static net.burndmg.eschallenges.infrastructure.util.RepositoryProjectionUtil.queryBuilderWithProjectionFor;


@RequiredArgsConstructor
public class PaginationRepositoryImpl implements PaginationRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public <T> Page<T> findAllAfter(PageSettings pageSettings, Class<T> projectionType, IndexCoordinates indexCoordinates) {
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

        SearchHits<T> result = elasticsearchOperations.search(query, projectionType, indexCoordinates);

        return Page.<T>builder()
                   .result(result.stream().map(SearchHit::getContent).toList())
                   .size(result.getSearchHits().size())
                   .lastSortValue(toLastSortValue(result))
                   .total(result.getTotalHits())
                   .build();
    }

    private static <T> Long toLastSortValue(SearchHits<T> result) {
        if (result.isEmpty()) {
            return null;
        }

        return (Long) result.getSearchHit(result.getSearchHits().size() - 1)
                            .getSortValues()
                            .get(0);
    }
}
