package net.burndmg.eschallenges.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "acceptances", createIndex = false)
public record ChallengeAcceptance (
        @Id
        String id,

        String username,
        String challengeId,
        String request,
        boolean successful,
        ChallengeAcceptanceFailedTest failedTest
) {}
