package net.burndmg.eschallenges.data.dto.participant;

import java.util.List;
import java.util.Map;

public record ParticipantChallenge (
        String id,
        String title,
        String description,
        List<String> examples,
        Map<String, Object> indexSettings
) {
}
