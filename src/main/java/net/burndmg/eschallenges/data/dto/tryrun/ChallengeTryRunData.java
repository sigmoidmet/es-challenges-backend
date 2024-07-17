package net.burndmg.eschallenges.data.dto.tryrun;

import lombok.Builder;

@Builder
public record ChallengeTryRunData(
        String challengeId,
        String jsonIndexedDataArray,
        String request,
        String username
) {}
