package net.burndmg.eschallenges.repository.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.Optional;

import static net.burndmg.eschallenges.infrastructure.util.RepositoryProjectionUtil.queryBuilderWithProjectionFor;

@RequiredArgsConstructor
public class ProjectionRepositoryImpl implements ProjectionRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public <T> Optional<T> findById(String id, Class<T> projectionType, IndexCoordinates indexCoordinates) {
        Query query = queryBuilderWithProjectionFor(projectionType)
                .withQuery(CriteriaQuery.builder(Criteria.where("id").is(id)).build())
                .build();
        SearchHit<T> searchHit = elasticsearchOperations.searchOne(query, projectionType, indexCoordinates);

        return Optional.ofNullable(searchHit)
                       .map(SearchHit::getContent);
    }
}
