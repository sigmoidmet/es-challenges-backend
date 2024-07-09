package net.burndmg.eschallenges.data.dto;

public record SortableHit<T> (
        T content,
        long sortValue
) {}
