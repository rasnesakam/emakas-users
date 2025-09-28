package com.emakas.userService.service;

import com.emakas.userService.dto.Response;
import com.emakas.userService.model.*;
import com.emakas.userService.repository.CoreRepository;
import com.emakas.userService.repository.ResourcePermissionRepository;
import com.emakas.userService.shared.data.PermissionDescriptor;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import com.emakas.userService.shared.enums.PermissionTargetType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResourcePermissionService extends CoreService<ResourcePermission, UUID> {
    private final ResourcePermissionRepository repository;
    private final ResourceService resourceService;
    private final TeamService teamService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final Logger logger = LoggerFactory.getLogger(ResourcePermissionService.class);

    public ResourcePermissionService(ResourcePermissionRepository repository, ResourceService resourceService, TeamService teamService, UserService userService, ApplicationService applicationService) {
        super(repository);
        this.repository = repository;
        this.resourceService = resourceService;
        this.teamService = teamService;
        this.userService = userService;
        this.applicationService = applicationService;
    }


    public Optional<ResourcePermission> registerResourcePermission(@NotNull ResourcePermission resourcePermission){
        try {
            resourcePermission.setResource(resourceService.getById(resourcePermission.getResource().getId()).orElseThrow(() -> new NoSuchElementException("No such resource found")));
            switch (resourcePermission.getPermissionTargetType()){
                case TEAM -> resourcePermission.setTeam(teamService.getById(resourcePermission.getTeam().getId()).orElseThrow(() -> new NoSuchElementException("No such team found")));
                case USER -> resourcePermission.setUser(userService.getById(resourcePermission.getUser().getId()).orElseThrow(() -> new NoSuchElementException("No such user found")));
                case APP -> resourcePermission.setApplication(applicationService.getById(resourcePermission.getApplication().getId()).orElseThrow(() -> new NoSuchElementException("No such application found")));
                default -> throw new NoSuchElementException("Unknown permission type");
            }
            return Optional.of(super.save(resourcePermission));
        }
        catch (NoSuchElementException exception){
            logger.error(exception.getLocalizedMessage());
            return Optional.empty();
        }
    }

    public Collection<ResourcePermission> getPermissionsByApplication(Application application) {
        return repository.findByApplicationId(application.getId());
    }

    public Collection<ResourcePermission> getPermissionsByTeam(Team team) {
        return repository.findByTeamId(team.getId());
    }

    public Collection<ResourcePermission> getPermissionsByUser(User user) {
        return repository.findByUserId(user.getId());
    }

    public Collection<ResourcePermission> getUserPermissionsByResource(User user, Resource resource) {
        return repository.findByUserIdAndResourceUri(user.getId(), resource.getUri());
    }
    public Collection<ResourcePermission> getTeamPermissionsByResource(Team team, Resource resource) {
        return repository.findByTeamIdAndResourceUri(team.getId(), resource.getUri());
    }
    public Collection<ResourcePermission> getApplicationPermissionsByResource(Application application, Resource resource) {
        return repository.findByApplicationIdAndResourceUri(application.getId(), resource.getUri());
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission, String targetDomainUri, PermissionDescriptor permissionDescriptor){
        boolean matchAccessModifiers = resourcePermission.getAccessModifier() == AccessModifier.READ_WRITE
                || ( permissionDescriptor.getAccessModifier().isPresent() && resourcePermission.getAccessModifier() == permissionDescriptor.getAccessModifier().get() );
        boolean matchPermissionScopes = resourcePermission.getPermissionScope() == PermissionScope.GLOBAL
                || ( permissionDescriptor.getPermissionScope().isPresent() && resourcePermission.getPermissionScope() == permissionDescriptor.getPermissionScope().get() );
        boolean matchResource = resourcePermission.getResource().getUri().equals(targetDomainUri);

        return matchResource && matchPermissionScopes && matchAccessModifiers;
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, PermissionDescriptor permissionDescriptor, User user){
        boolean matchPermission = hasPermissionFor(resourcePermission, targetDomainUri, permissionDescriptor);
        boolean matchIsForUser = resourcePermission.getPermissionTargetType() == PermissionTargetType.USER
                && resourcePermission.getUser().getId().equals(user.getId());
        return matchPermission && matchIsForUser;
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, PermissionDescriptor permissionDescriptor, Team team){
        boolean matchPermission = hasPermissionFor(resourcePermission, targetDomainUri, permissionDescriptor);
        boolean matchIsForTeam = resourcePermission.getPermissionTargetType() == PermissionTargetType.TEAM
                && resourcePermission.getUser().getId().equals(team.getId());
        return matchPermission && matchIsForTeam;
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, PermissionDescriptor permissionDescriptor, Application application){
        boolean matchPermission = hasPermissionFor(resourcePermission, targetDomainUri, permissionDescriptor);
        boolean matchIsForApp = resourcePermission.getPermissionTargetType() == PermissionTargetType.APP
                && resourcePermission.getUser().getId().equals(application.getId());
        return matchPermission && matchIsForApp;
    }
}
