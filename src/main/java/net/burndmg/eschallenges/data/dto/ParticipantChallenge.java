package net.burndmg.eschallenges.data.dto;

import java.util.List;

public record ParticipantChallenge (
        String id,
        String title,
        String description,
        List<String> examples,
        String indexSettings
) {
}
