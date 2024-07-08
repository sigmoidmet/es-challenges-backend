package net.burndmg.eschallenges.repository;

import java.util.List;
import java.util.Map;

public interface ChallengeRunRepository {

    void tryCreateIndex(String indexName, Map<String, Object> indexSettings);

    void tryDeleteIndex(String indexName);

    void saveAll(String indexName, List<Map<String, Object>> indexedData);

    List<Map<String, Object>> search(String indexName, String searchRequest);
}
