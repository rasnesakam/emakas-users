package com.emakas.userService.permissionEvaluators;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.model.*;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CorePermissionEvaluator implements PermissionEvaluator {
    private final ResourcePermissionService resourcePermissionService;
    private final UserService userService;
    private final TeamService teamService;
    private final ApplicationService applicationService;
    private final ResourceService resourceService;

    @Autowired
    public CorePermissionEvaluator(ResourcePermissionService resourcePermissionService, UserService userService, TeamService teamService, ApplicationService applicationService, ResourceService resourceService) {
        this.resourcePermissionService = resourcePermissionService;
        this.userService = userService;
        this.teamService = teamService;
        this.applicationService = applicationService;
        this.resourceService = resourceService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication instanceof JwtAuthentication) {
            String userId = authentication.getName();
            UserToken userToken = (UserToken) authentication.getCredentials();
            String resourceUri = targetDomainObject.toString();
            Resource resource = resourceService.getByUri(resourceUri);
            AccessModifier modifier = AccessModifier.valueOf(permission.toString());
            if (userToken.getTokenType() == TokenType.USR) {
                User user = userService.getById(UUID.fromString(userId));
                Collection<Team> userTeams = teamService.getUserTeams(user);
                boolean hasUserPermission = resourcePermissionService.getUserPermissionsByResource(user, resource).stream()
                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, user));
                boolean hasTeamPermission = userTeams.stream()
                        .anyMatch(team ->
                                resourcePermissionService.getTeamPermissionsByResource(team, resource).stream()
                                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, team))
                        );
                return hasUserPermission || hasTeamPermission;
            }
            else if (userToken.getTokenType() == TokenType.APP) {
                Application app = applicationService.getById(UUID.fromString(userId));
                return resourcePermissionService.getApplicationPermissionsByResource(app, resource).stream()
                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, app));
            }
            else return false;
        }
        else return false;

    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
