package net.burndmg.eschallenges.map;

import net.burndmg.eschallenges.data.dto.run.ChallengeAcceptanceDto;
import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeAcceptanceMapper {

    ChallengeAcceptanceDto toDto(ChallengeAcceptance challengeAcceptance);
}
