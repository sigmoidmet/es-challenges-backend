package net.burndmg.eschallenges.data.dto.participant;

import lombok.Builder;
import net.burndmg.eschallenges.data.dto.Page;

@Builder
public record ParticipantChallengePage (
        Page<ParticipantChallengePreview> challenges
) {}
