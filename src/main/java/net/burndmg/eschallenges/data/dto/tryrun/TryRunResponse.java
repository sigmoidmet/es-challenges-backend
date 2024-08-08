package net.burndmg.eschallenges.data.dto.tryrun;

import lombok.Builder;
import net.burndmg.eschallenges.data.dto.run.RunSearchResponse;

@Builder
public record TryRunResponse (
        RunSearchResponse expectedResponse,
        RunSearchResponse actualResponse,
        boolean isSuccessful
) {}
