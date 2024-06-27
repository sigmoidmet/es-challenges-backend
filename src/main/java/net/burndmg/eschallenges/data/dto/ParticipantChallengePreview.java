package net.burndmg.eschallenges.data.dto;

import lombok.Builder;

@Builder
public record ParticipantChallengePreview (
        String id,
        String title
) {}
