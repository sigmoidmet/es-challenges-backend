package net.burndmg.eschallenges.data.dto;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

public record PageSettings (

        @RequestParam(required = false)
        Sort.Direction direction,

        @RequestParam(required = false)
        Integer size,

        @RequestParam(required = false)
        Long searchAfter
) {
        // Spring's defaultValue isn't supported for records
        public PageSettings {
                if (direction == null) {
                        direction = Sort.Direction.ASC;
                }

                if (size == null) {
                        size = 30;
                }
        }
}
