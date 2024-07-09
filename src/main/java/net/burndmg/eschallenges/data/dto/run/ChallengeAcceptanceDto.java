package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;
import net.burndmg.eschallenges.data.model.ChallengeAcceptanceFailedTest;

@Builder
public record ChallengeAcceptanceDto(

        String id,

        String username,
        String challengeId,

        String request,
        boolean successful,
        ChallengeAcceptanceFailedTest failedTest
) {}
