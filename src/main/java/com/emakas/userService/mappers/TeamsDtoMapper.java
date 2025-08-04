package com.emakas.userService.mappers;

import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.dto.TeamWriteDto;
import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TeamsDtoMapper {
    static TeamsDtoMapper getInstance() { return Mappers.getMapper(TeamsDtoMapper.class); }

    public abstract TeamReadDto toTeamDto(Team team);
    public abstract Team toTeam(TeamReadDto teamReadDto);

    @Mapping(target="parentTeam", ignore = true)
    @Mapping(target="childTeams", ignore = true)
    @Mapping(target="members", ignore = true)
    @Mapping(target="lead", ignore = true)
    public abstract Team toTeam(TeamWriteDto teamWriteDto, @Context UserService userService, @Context TeamService teamService, @Context User user);

    @AfterMapping
    protected void afterMapping(@MappingTarget Team team, TeamWriteDto teamWriteDto, @Context UserService userService, @Context TeamService teamService, @Context User user){
        if(teamWriteDto.getParentTeam() != null){
            Optional<Team> parentTeamValue = teamService.getById(teamWriteDto.getParentTeam());
            parentTeamValue.ifPresent(team::setParentTeam);
        }
        if (teamWriteDto.getMembers() != null && !teamWriteDto.getMembers().isEmpty()){
            Collection<User> members = teamWriteDto.getMembers().stream()
                    .map(userService::getById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            team.setMembers(members);
        }
        if (user != null){
            team.setLead(user);
        }
    }
}
