package net.burndmg.eschallenges.data.dto.participant;

import net.burndmg.eschallenges.data.model.ChallengeExample;

import java.util.List;

public record ParticipantChallenge (
        String id,
        String title,
        String description,
        List<ChallengeExample> examples,
        String jsonIndexSettings
) {
}
