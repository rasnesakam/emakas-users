package com.emakas.userService.controller;

import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.mappers.TeamsDtoMapper;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.shared.enums.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/teams")
public class TeamsController {
    private final TeamService teamService;
    private final TeamsDtoMapper teamsDtoMapper;

    @Autowired
    public TeamsController(TeamService teamService, TeamsDtoMapper teamsDtoMapper) {
        this.teamService = teamService;
        this.teamsDtoMapper = teamsDtoMapper;
    }

    @GetMapping("/owned")
    public ResponseEntity<Collection<TeamReadDto>> getOwnedTeams(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userPrincipal = (String) authentication.getPrincipal();
        Optional<UUID> userId = TokenType.getCleanSubject(TokenType.USR,userPrincipal).map(UUID::fromString);
        if (userId.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Collection<TeamReadDto> teams = teamService.getTeamsByOwner(userId.get()).stream().map(teamsDtoMapper::teamToTeamReadDto).toList();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }
}
