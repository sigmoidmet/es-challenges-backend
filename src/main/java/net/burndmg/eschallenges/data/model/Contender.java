package net.burndmg.eschallenges.data.model;

import lombok.Builder;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Builder
@Document(indexName = Contender.READ_INDEX_NAME, createIndex = false)
public record Contender (
        @Id
        String id,

        String username,

        List<String> authorities
) {

        public static final String READ_INDEX_NAME = "contenders-read";
        public static final String UPDATE_INDEX_NAME = "contenders-update";
}
