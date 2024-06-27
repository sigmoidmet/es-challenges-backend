package net.burndmg.eschallenges.mapping;

import net.burndmg.eschallenges.data.dto.ParticipantChallenge;
import net.burndmg.eschallenges.data.dto.ParticipantChallengePreview;
import net.burndmg.eschallenges.data.model.Challenge;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeMapper {

    ParticipantChallenge toParticipantChallenge(Challenge challenge);

    ParticipantChallengePreview toParticipantChallengePreview(Challenge challenge);
}
