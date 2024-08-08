package net.burndmg.eschallenges.repository.run;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.run.RunSearchHit;
import net.burndmg.eschallenges.data.dto.run.RunSearchResponse;
import net.burndmg.eschallenges.data.dto.run.RunSearchResponseBody;
import net.burndmg.eschallenges.infrastructure.expection.instance.InvalidRequestException;
import net.burndmg.eschallenges.infrastructure.util.ObjectMapperWrapper;
import org.apache.http.HttpStatus;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveIndexOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChallengeRunRepositoryImpl implements ChallengeRunRepository {

    private final RestClient restClient;
    private final ReactiveElasticsearchOperations elasticsearchOperations;
    private final ObjectMapperWrapper objectMapper;


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
    public Mono<RunSearchResponse> search(String indexName, String searchRequest) {
        Request request = new Request("POST", "/" + indexName + "/_search");
        request.setJsonEntity(searchRequest);

        return searchForBody(request)
                .onErrorMap(this::isBadRequest)
                .map(response -> new RunSearchResponse(toHits(response), response.aggregations()));
    }

    private Mono<RunSearchResponseBody> searchForBody(Request request) {
        return Mono.<String>create(sink -> restClient.performRequestAsync(request, new MonoAsyncSearchAdapter(sink)))
                   .map(json -> objectMapper.readValue(json, RunSearchResponseBody.class));
    }

    private Throwable isBadRequest(Throwable e) {
        try {
            if (e instanceof ResponseException responseException &&
                responseException.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                return new InvalidRequestException(new String(responseException.getResponse()
                                                                               .getEntity()
                                                                               .getContent()
                                                                               .readAllBytes()));
            }
        } catch (IOException ioException) {
            ioException.addSuppressed(e);
            return ioException;
        }
        return e;
    }

    private static List<Map<String, Object>> toHits(RunSearchResponseBody response) {
        return response.hits()
                       .hits()
                       .stream()
                       .map(RunSearchHit::source)
                       .toList();
    }
}
