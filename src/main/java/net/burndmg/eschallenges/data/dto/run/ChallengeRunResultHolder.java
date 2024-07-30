package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;

@Builder
public record ChallengeRunResultHolder(
        ChallengeRunResult result,
        boolean isSuccessful
) {}
