package net.burndmg.eschallenges.data.dto.tryrun;

import java.util.List;
import java.util.Map;

public record TryRunRequest (
        List<Map<String, Object>> indexedData,
        String request
) {}
