package net.burndmg.eschallenges.data.dto;

import lombok.Builder;

@Builder
public record ParticipantChallengePage (
        Page<ParticipantChallengePreview> challenges
) {}
