package net.burndmg.eschallenges.repository;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpMappingException;
import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.infrastructure.expection.instance.InvalidRequestException;
import org.springframework.data.elasticsearch.client.elc.EntityAsMap;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveIndexOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChallengeRunRepositoryImpl implements ChallengeRunRepository {

    private final ReactiveElasticsearchOperations elasticsearchOperations;
    private final ReactiveElasticsearchClient elasticsearchClient;


    @Override
    public Mono<Boolean> createIndex(String indexName, @Nullable Map<String, Object> indexMappings) {
        ReactiveIndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));

        if (indexMappings == null) {
            return indexOperations.create();
        }


        return indexOperations.create(Map.of(), Document.from(indexMappings));
    }

    @Override
    public Mono<Boolean> deleteIndex(String indexName) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).delete();
    }

    @Override
    public Mono<Void> saveAll(String indexName, List<Map<String, Object>> indexedData) {
        return elasticsearchOperations.withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                                      .saveAll(indexedData, IndexCoordinates.of(indexName))
                                      .then();
    }

    @Override
    public Mono<List<Map<String, Object>>> search(String indexName, String searchRequest) {
        return elasticsearchClient
                .search(toRequest(indexName, searchRequest), EntityAsMap.class)
                .map(response -> response.hits()
                                         .hits()
                                         .stream()
                                         .map(Hit::source)
                                         .map(map -> (Map<String, Object>) map)
                                         .toList());
    }

    private SearchRequest toRequest(String indexName, String searchRequest) {
        try {
            return SearchRequest.of(fn -> fn.index(indexName).withJson(new StringReader(searchRequest)));
        } catch (JsonpMappingException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
