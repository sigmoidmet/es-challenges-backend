package net.burndmg.eschallenges.data.dto.run;

import lombok.Builder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Builder
public record RunTest (
        String username,
        Map<String, Object> indexMappings,
        Mono<List<Map<String, Object>>> expectedResult,
        List<Map<String, Object>> jsonTestArray,
        String userRequest,
        boolean resultShouldBeOrdered
) {}
