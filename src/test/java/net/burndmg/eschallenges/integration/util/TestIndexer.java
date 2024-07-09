package net.burndmg.eschallenges.integration.util;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.elasticsearch.core.RefreshPolicy;

import java.util.UUID;

@TestComponent
@RequiredArgsConstructor
public class TestIndexer {

    private final ChallengeRepository challengeRepository;

    public void cleanUpIndex() {
        challengeRepository.deleteAll(RefreshPolicy.IMMEDIATE).block();
    }

    public Challenge indexRandomChallengeAndReturnIt(String id) {
        Challenge challenge = Challenge.builder()
                                       .id(id)
                                       .title(UUID.randomUUID().toString())
                                       .description(UUID.randomUUID().toString())
                                       .build();
        challenge = indexChallenge(challenge);

        return challenge;
    }

    public Challenge indexChallenge(Challenge challenge) {
        return challengeRepository.save(challenge, RefreshPolicy.IMMEDIATE).block();
    }
}
