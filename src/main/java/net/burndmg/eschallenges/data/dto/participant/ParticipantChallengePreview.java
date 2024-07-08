package net.burndmg.eschallenges.data.dto.participant;

import lombok.Builder;

@Builder
public record ParticipantChallengePreview (
        String id,
        String title
) {}
