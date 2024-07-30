package net.burndmg.eschallenges.integration.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.PutAliasRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.elasticsearch.core.RefreshPolicy;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@TestComponent
@RequiredArgsConstructor
public class TestIndexer {

    private final TestChallengeRepository challengeRepository;
    private final ElasticsearchClient elasticsearchClient;

    @SneakyThrows
    public void cleanUpIndex() {
        // doing a refresh before delete by query, so it would notice some not yet refreshed documents
        // created by previous tests in application logic which doesn't have any manual refreshing mechanism
        elasticsearchClient.indices().refresh(builder -> builder.index(Challenge.READ_INDEX_NAME));

        switchUpdateAliasTo(Challenge.READ_INDEX_NAME);

        challengeRepository.deleteAll(RefreshPolicy.IMMEDIATE);
    }

    @SneakyThrows
    public void switchUpdateAliasTo(String destination) {
        List<String> indices = getIndicesListOrCreateNewIndex(destination);

        elasticsearchClient.indices().deleteAlias(
                DeleteAliasRequest.of(builder -> builder.index("*").name(Challenge.UPDATE_INDEX_NAME))
        );
        elasticsearchClient.indices().putAlias(PutAliasRequest.of(
                builder -> builder.index(indices).isWriteIndex(true).name(Challenge.UPDATE_INDEX_NAME)
        ));
    }

    private List<String> getIndicesListOrCreateNewIndex(String destination) throws IOException {
        try {
            return elasticsearchClient.indices()
                                      .getAlias(GetAliasRequest.of(builder -> builder.index(destination)))
                                      .result()
                                      .keySet()
                                      .stream()
                                      .toList();
        } catch (ElasticsearchException e) {
            if (e.getMessage().contains("[index_not_found_exception]")) {
                elasticsearchClient.indices().create(CreateIndexRequest.of(builder -> builder.index(destination)));
                return List.of(destination);
            }
            throw e;
        }
    }

    public Challenge indexRandomChallengeAndReturnIt(String id) {
        Challenge challenge = Challenge.builder()
                                       .id(id)
                                       .title(UUID.randomUUID().toString())
                                       .description(UUID.randomUUID().toString())
                                       .jsonChallengeTestArray(UUID.randomUUID().toString())
                                       .jsonChallengeTestArray(UUID.randomUUID().toString())
                                       .idealRequest(UUID.randomUUID().toString())
                                       .jsonIndexSettings(UUID.randomUUID().toString())
                                       .examples(List.of(new ChallengeExample(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString())))
                                       .build();

        challenge = indexChallenge(challenge);

        return challenge;
    }

    public Challenge indexChallenge(Challenge challenge) {
        return challengeRepository.save(challenge, RefreshPolicy.IMMEDIATE);
    }
}
