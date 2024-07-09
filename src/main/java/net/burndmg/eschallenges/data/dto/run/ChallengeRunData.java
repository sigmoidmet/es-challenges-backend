package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

@Builder
public record ChallengeRunData(
        String challengeId,
        String request,
        String username
) {}
