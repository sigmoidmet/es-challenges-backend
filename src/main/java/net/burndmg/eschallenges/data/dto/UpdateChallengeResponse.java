package net.burndmg.eschallenges.data.dto;

public record UpdateChallengeResponse (
        String id,
        boolean isDuringReindexingProcess
) {}
