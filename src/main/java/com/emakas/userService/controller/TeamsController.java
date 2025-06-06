package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.mappers.TeamsDtoMapper;
import com.emakas.userService.model.Team;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TokenManager tokenManager;

    @Autowired
    public TeamsController(TeamService teamService, TeamsDtoMapper teamsDtoMapper, TokenManager tokenManager) {
        this.teamService = teamService;
        this.teamsDtoMapper = teamsDtoMapper;
        this.tokenManager = tokenManager;
    }

    @PreAuthorize("hasPermission('iam.emakas.net/teams','read')")
    @GetMapping("/owned")
    public ResponseEntity<Response<Collection<TeamReadDto>>> getOwnedTeams(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            String userPrincipal = (String) jwtAuthentication.getPrincipal();
            UUID userId = UUID.fromString(userPrincipal);
            if (jwtAuthentication.getUserToken().getTokenType() != TokenType.USR)
                return new ResponseEntity<>(Response.of("Invalid Token Type"), HttpStatus.UNAUTHORIZED);
            Collection<Team> teams = teamService.getTeamsByOwner(userId);
            return new ResponseEntity<>(Response.of(teams.stream().map(teamsDtoMapper::teamToTeamReadDto).toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(Response.of("Invalid Token"), HttpStatus.UNAUTHORIZED);
    }
}
