package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TeamReadDto;
import com.emakas.userService.dto.TeamWriteDto;
import com.emakas.userService.mappers.TeamsDtoMapper;
import com.emakas.userService.model.Team;
import com.emakas.userService.model.User;
import com.emakas.userService.service.TeamService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {
    private final TeamService teamService;
    private final TeamsDtoMapper teamsDtoMapper;
    private final TokenManager tokenManager;
    private final Logger logger = LoggerFactory.getLogger(TeamsController.class);
    private final UserService userService;

    @Autowired
    public TeamsController(TeamService teamService, TeamsDtoMapper teamsDtoMapper, TokenManager tokenManager, UserService userService) {
        this.teamService = teamService;
        this.teamsDtoMapper = teamsDtoMapper;
        this.tokenManager = tokenManager;
        this.userService = userService;
    }

    @Operation(summary = "Create new team", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasPermission(#RSC_TEAMS,'self:write')")
    @PostMapping("/new")
    public ResponseEntity<Response<TeamReadDto>> createNewTeam(@RequestBody TeamWriteDto teamWriteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication){
            Optional<User> leadUserValue = tokenManager.loadUserFromToken(jwtAuthentication.getUserToken());
            if (leadUserValue.isPresent()){
                Team team = teamsDtoMapper.toTeam(teamWriteDto, userService, teamService, leadUserValue.get());
                try {
                    Team newTeam = teamService.save(team);
                    URI teamUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                            .path("/{id}")
                            .buildAndExpand(newTeam.getId())
                            .toUri();
                    return ResponseEntity.created(teamUri).body(Response.of(teamsDtoMapper.toTeamDto(newTeam)));
                }
                catch(IllegalArgumentException illegalArgumentException){
                    logger.error(illegalArgumentException.getLocalizedMessage());
                    return ResponseEntity.badRequest().body(Response.of(illegalArgumentException.getMessage()));
                }
                catch(OptimisticLockingFailureException optimisticLockingFailureException){
                    logger.error(optimisticLockingFailureException.getLocalizedMessage());
                    return ResponseEntity.internalServerError().body(Response.of(optimisticLockingFailureException.getMessage()));
                }
            }
        }
        logger.warn("Trying to create new team with invalid token");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.of("Try with valid token"));
    }

    @Operation(summary = "Get the teams that you ownded", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasPermission(#RSC_TEAMS,'self:read')")
    @GetMapping("/owned")
    public ResponseEntity<Collection<TeamReadDto>> getOwnedTeams(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            String userPrincipal = (String) jwtAuthentication.getPrincipal();
            UUID userId = UUID.fromString(userPrincipal);
            if (jwtAuthentication.getUserToken().getTokenType() != TokenType.USR)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            Collection<Team> teams = teamService.getTeamsByOwner(userId);
            return ResponseEntity.ok(teams.stream().map(teamsDtoMapper::toTeamDto).toList());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
