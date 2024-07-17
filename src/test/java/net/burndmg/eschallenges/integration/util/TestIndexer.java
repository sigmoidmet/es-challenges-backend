package net.burndmg.eschallenges.integration.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.elasticsearch.core.RefreshPolicy;

import java.util.List;
import java.util.UUID;


@TestComponent
@RequiredArgsConstructor
public class TestIndexer {

    private final ChallengeRepository challengeRepository;
    private final ElasticsearchClient elasticsearchClient;

    @SneakyThrows
    public void cleanUpIndex() {
        // doing a refresh before delete by query, so it would notice some not yet refreshed documents
        // created by previous tests in application logic which doesn't have any manual refreshing mechanism
        elasticsearchClient.indices().refresh(builder -> builder.index(Challenge.INDEX_NAME));
        challengeRepository.deleteAll(RefreshPolicy.IMMEDIATE).block();
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
        return challengeRepository.save(challenge, RefreshPolicy.IMMEDIATE).block();
    }
}
