package com.emakas.userService.controller;

import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.TeamsDtoMapper;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MembersController {
    private final UserService userService;
    private final TeamService teamService;
    private final UserDtoMapper userDtoMapper;
    private final TeamsDtoMapper teamsDtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(MembersController.class);


    @Autowired
    public MembersController(UserService userService, TeamService teamService, UserDtoMapper userDtoMapper, TeamsDtoMapper teamsDtoMapper) {
        this.userService = userService;
        this.teamService = teamService;
        this.userDtoMapper = userDtoMapper;
        this.teamsDtoMapper = teamsDtoMapper;
    }

    @Operation(summary = "Assign new member to team", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasPermission(#RSC_TEAM_MEMBERS, 'self:write')")
    @PostMapping("/assign/{team_uri}")
    public ResponseEntity<TeamReadDto> assignMemberOwnTeam(@RequestBody UserWriteDto userWriteDto, @PathVariable("team_uri") String teamUri) {
        Optional<Team> intendedTeam = teamService.getByUri(teamUri);
        return intendedTeam.map(team -> {
            Optional<User> foundedUser = userService.getByUserName(userWriteDto.getUserName());
            return foundedUser.map(user -> {
                String returnMessage = String.format("User %s has assigned to team %s.", user.getUserName(), team.getName());
                logger.info(returnMessage);
                Team newTeam = teamService.addMemberToTeam(team, user);
                return ResponseEntity.ok(teamsDtoMapper.toTeamDto(newTeam));
            }).orElseGet(() -> ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Team Information", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasPermission(#RSC_TEAM_MEMBERS, 'self:read')")
    @GetMapping("{team_uri}")
    public ResponseEntity<TeamReadDto> getTeam(@PathVariable("team_uri") String teamUri){
        Optional<Team> requestedTeam = teamService.getByUri(teamUri);
        return requestedTeam.map(team -> ResponseEntity.ok(teamsDtoMapper.toTeamDto(team)))
                .orElse(ResponseEntity.notFound().build());
    }
}
