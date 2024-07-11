package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Map;

@Document(indexName = Challenge.INDEX_NAME, createIndex = false)
@Builder
public record Challenge (

        @Id
        String id,

        String title,

        String description,

        @Field(type = FieldType.Object)
        Map<String, Object> indexSettings,

        String idealRequest,

        @Singular
        @Field(type = FieldType.Object)
        List<ChallengeTest> challengeTests,

        @Field(type = FieldType.Object)
        List<ChallengeExample> examples,

        String timestamp
) implements TimestampBasedSortable {

        public static final String INDEX_NAME = "challenges";
}
