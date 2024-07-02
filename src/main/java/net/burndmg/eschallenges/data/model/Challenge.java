package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = Challenge.INDEX_NAME, createIndex = false)
@Builder
public record Challenge (

        @Id
        String id,

        String title,

        String description,

        String indexSettings,

        List<String> testsDataJson,

        List<ChallengeExample> examples,

        String timestamp
) implements TimestampBasedSortable {

        public static final String INDEX_NAME = "challenges";
}
