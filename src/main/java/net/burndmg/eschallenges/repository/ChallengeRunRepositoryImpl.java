package net.burndmg.eschallenges.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpMappingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.burndmg.eschallenges.infrastructure.expection.instance.InvalidRequestException;
import org.springframework.data.elasticsearch.client.elc.EntityAsMap;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChallengeRunRepositoryImpl implements ChallengeRunRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;


    @Override
    public void tryCreateIndex(String indexName, @Nullable Map<String, Object> indexSettings) {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));

        if (indexSettings == null) {
            indexOperations.create();
            return;
        }

        indexOperations.create(indexSettings);
    }

    @Override
    public void tryDeleteIndex(String indexName) {
        elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).delete();
    }

    @Override
    public void saveAll(String indexName, List<Map<String, Object>> indexedData) {
        elasticsearchOperations.withRefreshPolicy(RefreshPolicy.IMMEDIATE).save(indexedData, IndexCoordinates.of(indexName));
    }

    @Override
    @SneakyThrows
    public List<Map<String, Object>> search(String indexName, String searchRequest) {
        SearchResponse<EntityAsMap> search = elasticsearchClient.search(
                toRequest(indexName, searchRequest),
                EntityAsMap.class
        );

        return search.hits().hits()
                     .stream()
                     .map(Hit::source)
                     .map(map -> (Map<String, Object>) map)
                     .toList();
    }

    private static SearchRequest toRequest(String indexName, String searchRequest) {
        try {
            return SearchRequest.of(fn -> fn.index(indexName).withJson(new StringReader(searchRequest)));
        } catch (JsonpMappingException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
