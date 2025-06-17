package com.emakas.userService.permissionEvaluators;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.model.*;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.TokenType;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Deprecated
// @Component
public class CorePermissionEvaluator implements PermissionEvaluator {
    private final ResourcePermissionService resourcePermissionService;
    private final UserService userService;
    private final TeamService teamService;
    private final ApplicationService applicationService;
    private final ResourceService resourceService;

    // @Autowired
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
            Token token = (Token) authentication.getCredentials();
            String resourceUri = targetDomainObject.toString();
            Resource resource = resourceService.getByUri(resourceUri);
            AccessModifier modifier = AccessModifier.valueOf(permission.toString());
            if (token.getTokenType() == TokenType.USR) {
                Optional<User> user = userService.getById(UUID.fromString(userId));
                if (user.isEmpty())
                    return false;
                Collection<Team> userTeams = teamService.getUserTeams(user.get());
                boolean hasUserPermission = resourcePermissionService.getUserPermissionsByResource(user.get(), resource).stream()
                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, user.get()));
                boolean hasTeamPermission = userTeams.stream()
                        .anyMatch(team ->
                                resourcePermissionService.getTeamPermissionsByResource(team, resource).stream()
                                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, team))
                        );
                return hasUserPermission || hasTeamPermission;
            }
            else if (token.getTokenType() == TokenType.APP) {
                Optional<Application> app = applicationService.getById(UUID.fromString(userId));
                return app.isPresent() && resourcePermissionService.getApplicationPermissionsByResource(app.get(), resource).stream()
                        .anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, resourceUri, modifier, app.get()));
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
