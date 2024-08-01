package net.burndmg.eschallenges.map;

import net.burndmg.eschallenges.data.dto.SaveChallengeDto;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "tests", source = "tests")
    Challenge toModel(String id, SaveChallengeDto challengeDto, List<ChallengeTest> tests);

    @Mapping(target = "tests", source = "tests")
    Challenge toModel(SaveChallengeDto challengeDto,  List<ChallengeTest> tests);
}
