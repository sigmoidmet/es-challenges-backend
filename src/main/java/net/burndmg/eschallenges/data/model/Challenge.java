package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Map;

@Document(indexName = Challenge.INDEX_NAME, createIndex = false)
@Builder
public record Challenge (

        @Id
        String id,

        String title,

        String description,

        Map<String, Object> indexSettings,

        String idealRequest,

        List<Map<String, Object>> testsDataJson,

        List<ChallengeExample> examples,

        String timestamp
) implements TimestampBasedSortable {

        public static final String INDEX_NAME = "challenges";
}
