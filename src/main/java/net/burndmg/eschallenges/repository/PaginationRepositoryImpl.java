package net.burndmg.eschallenges.repository;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.TimestampBasedSortable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.List;


@RequiredArgsConstructor
public class PaginationRepositoryImpl<T extends TimestampBasedSortable> implements PaginationRepository<T> {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<T> findAllAfter(PageSettings pageSettings, Class<T> type) {
        Sort sortByTimestamp = Sort.by("timestamp");

        if (pageSettings.direction().isDescending()) {
            sortByTimestamp = sortByTimestamp.descending();
        }

        Query query = StringQuery.builder(StringQuery.MATCH_ALL)
                                 .withMaxResults(pageSettings.size())
                                 .build()
                                 .addSort(sortByTimestamp);

        if (pageSettings.searchAfter() != null) {
            query.setSearchAfter(List.of(pageSettings.searchAfter()));
        }

        SearchHits<T> result = elasticsearchOperations.search(query, type);

        return Page.<T>builder()
                   .result(result.stream().map(SearchHit::getContent).toList())
                   .size(result.getSearchHits().size())
                   .lastSortValue(toLastSortValue(result))
                   .total(result.getTotalHits())
                   .build();
    }

    private static <T extends TimestampBasedSortable> Long toLastSortValue(SearchHits<T> result) {
        if (result.isEmpty()) {
            return null;
        }

        return (Long) result.getSearchHit(result.getSearchHits().size() - 1)
                            .getSortValues()
                            .get(0);
    }
}
