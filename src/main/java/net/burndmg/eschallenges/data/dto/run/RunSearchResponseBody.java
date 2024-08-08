package net.burndmg.eschallenges.data.dto.run;

import java.util.Map;

public record RunSearchResponseBody(
        RunSearchHitContainer hits,
        Map<String, Object> aggregations
) {

    public RunSearchResponseBody {
        if (aggregations == null) {
            aggregations = Map.of();
        }
    }
}
