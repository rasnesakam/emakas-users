package com.emakas.userService.mappers;

import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TeamsDtoMapper {
    static TeamsDtoMapper getInstance() { return Mappers.getMapper(TeamsDtoMapper.class); }

    TeamReadDto teamToTeamReadDto(Team team);
}
