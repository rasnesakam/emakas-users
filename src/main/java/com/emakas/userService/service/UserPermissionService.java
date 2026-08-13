package com.emakas.userService.service;

import com.emakas.userService.config.OidcConfig;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.User;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserPermissionService {
    private final UserService userService;
    private final TeamService teamService;
    private final ResourcePermissionService resourcePermissionService;
    private final OidcConfig oidcConfig;
    private final StringToResourcePermissionConverter converter;


    @Autowired
    public UserPermissionService(UserService userService, TeamService teamService, ResourcePermissionService resourcePermissionService, @Lazy OidcConfig oidcConfig, StringToResourcePermissionConverter converter) {
        this.userService = userService;
        this.teamService = teamService;
        this.resourcePermissionService = resourcePermissionService;
        this.oidcConfig = oidcConfig;
        this.converter = converter;
    }

    private Optional<ResourcePermission> setResourcePermissionsByAllowed(
            ResourcePermission rp,
            Map<String, List<ResourcePermission>> permissionsByResource
    ) {
        return permissionsByResource
                .getOrDefault(rp.getResource().getUri(), List.of())
                .stream()
                .filter(gp -> gp.getAccessModifier()
                        .doesCover(rp.getAccessModifier()))
                .findFirst()
                .map(gp -> {
                    rp.setPermissionScope(gp.getPermissionScope());
                    rp.setAccessModifier(gp.getAccessModifier());
                    return rp;
                });
    }

    public String[] modifyScopesByUserPrivillage(User user, Set<String> scopes) {
        Stream<ResourcePermission> userPermissions = resourcePermissionService.getPermissionsByUser(user).stream();

        Stream<ResourcePermission> teamPermissions = teamService.getUserTeams(user)
                .stream().flatMap(teams -> resourcePermissionService.getPermissionsByTeam(teams).stream());

        Map<String, List<ResourcePermission>> totalPermissions = Stream.concat(userPermissions, teamPermissions)
                .collect(Collectors.groupingBy(rp -> rp.getResource().getUri()));

        return scopes.stream()
                .map(converter::convert)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(rp -> setResourcePermissionsByAllowed(
                        rp,
                        totalPermissions
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ResourcePermission::toString)
                .toArray(String[]::new);

    }
}
