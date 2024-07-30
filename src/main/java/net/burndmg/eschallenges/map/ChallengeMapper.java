package net.burndmg.eschallenges.map;

import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.model.Challenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeMapper {

    Challenge toModel(ChallengeDto challengeDto);

    @Mapping(target = "id", source = "id")
    Challenge toModel(String id, ChallengeDto challengeDto);
}
