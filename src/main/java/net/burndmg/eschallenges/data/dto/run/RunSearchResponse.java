package net.burndmg.eschallenges.data.dto.run;

import java.util.List;
import java.util.Map;

public record RunSearchResponse (
   List<Map<String, Object>> hits,
   Map<String, Object> aggregations
) {}
