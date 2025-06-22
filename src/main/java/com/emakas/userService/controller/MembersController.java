package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MembersController {
    private final UserService userService;
    private final TeamService teamService;
    private final UserDtoMapper userDtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(MembersController.class);


    @Autowired
    public MembersController(UserService userService, TeamService teamService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.teamService = teamService;
        this.userDtoMapper = userDtoMapper;
    }

    @PreAuthorize("hasPermission(#RSC_TEAM_MEMBERS, 'self:create')")
    @PostMapping("/assign/{team_uri}")
    public ResponseEntity<Response<Team>> assignMemberOwnTeam(@RequestBody UserWriteDto userWriteDto, @PathVariable("team_uri") String teamUri) {
        Optional<Team> intendedTeam = teamService.getByUri(teamUri);
        return intendedTeam.map(team -> {
            Optional<User> foundedUser = userService.getByUserName(userWriteDto.getUserName());
            return foundedUser.map(user -> {
                logger.info("Founded user will invite to the team {}", team.getName());
                Team newTeam = teamService.addMemberToTeam(team, user);
                return new ResponseEntity<>(Response.of(newTeam), HttpStatus.OK);
            }).orElseGet(() ->
                new ResponseEntity<>(Response.of("Team couldn't find"), HttpStatus.NOT_FOUND));
        }).orElse(new ResponseEntity<>(Response.of("Team couldn't find"), HttpStatus.NOT_FOUND));
    }
}
