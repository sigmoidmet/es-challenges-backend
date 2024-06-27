package net.burndmg.eschallenges.data.dto;

import lombok.Builder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Builder
public record Page<T> (
        List<T> result,
        int size,
        long total,
        @Nullable Long lastSortValue
) {

    public <R> Page<R> map(Function<T, R> mapper) {
        return Page.<R>builder()
                   .result(result.stream().map(mapper).toList())
                   .size(size)
                   .total(total)
                   .lastSortValue(lastSortValue)
                   .build();
    }
}
