package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "challenges")
@Builder
public record Challenge (

        @Id
        String id,

        String title,

        String description,

        List<String> examples,

        String indexSettings,

        String timestamp
) implements TimestampBasedSortable {}
