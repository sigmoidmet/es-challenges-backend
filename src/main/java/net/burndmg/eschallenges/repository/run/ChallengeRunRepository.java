package net.burndmg.eschallenges.repository.run;

import net.burndmg.eschallenges.data.dto.run.RunSearchResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ChallengeRunRepository {

    Mono<Boolean> createIndex(String indexName, Map<String, Object> indexSettings);

    Mono<Boolean> deleteIndex(String indexName);

    Mono<Void> saveAll(String indexName, List<Map<String, Object>> indexedData);

    Mono<RunSearchResponse> search(String indexName, String searchRequest);
}
