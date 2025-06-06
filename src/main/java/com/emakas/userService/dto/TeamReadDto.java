package com.emakas.userService.dto;

import java.util.Collection;

public class TeamReadDto {
    private String name;
    private String description;
    private String uri;
    private TeamReadDto parentTeam;
    private Collection<TeamReadDto> childTeams;
    private Collection<UserReadDto> users;
    private UserReadDto lead;
}
