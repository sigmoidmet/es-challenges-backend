package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = Challenge.INDEX_NAME, createIndex = false)
@Builder
public record Challenge (

        @Id
        String id,

        String title,

        String description,

        String jsonIndexSettings,

        String idealRequest,

        @Singular
        List<String> jsonChallengeTestArrays,

        @Field(type = FieldType.Object)
        List<ChallengeExample> examples,

        String timestamp
) implements TimestampBasedSortable {

        public static final String INDEX_NAME = "challenges";
}
