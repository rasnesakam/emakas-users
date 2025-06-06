package com.emakas.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamReadDto {
    private String name;
    private String description;
    private String uri;
    private TeamReadDto parentTeam;
    private Collection<TeamReadDto> childTeams;
    private Collection<UserReadDto> members;
    private UserReadDto lead;
}
