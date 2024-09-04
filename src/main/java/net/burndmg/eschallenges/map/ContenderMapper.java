package net.burndmg.eschallenges.map;

import net.burndmg.eschallenges.data.dto.ContenderAuthentication;
import net.burndmg.eschallenges.data.model.Contender;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContenderMapper {


    ContenderAuthentication toAuthentication(Contender contender);
}
