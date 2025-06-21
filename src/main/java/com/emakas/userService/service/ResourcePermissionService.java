package com.emakas.userService.service;

import com.emakas.userService.model.*;
import com.emakas.userService.repository.CoreRepository;
import com.emakas.userService.repository.ResourcePermissionRepository;
import com.emakas.userService.shared.data.PermissionDescriptor;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionTargetType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class ResourcePermissionService extends CoreService<ResourcePermission, UUID> {
    private final ResourcePermissionRepository repository;
    public ResourcePermissionService(ResourcePermissionRepository repository) {
        super(repository);
        this.repository = repository;
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
                || ( permissionDescriptor.getAccessModifier().isPresent()
                        && resourcePermission.getAccessModifier() == permissionDescriptor.getAccessModifier().get() );
        boolean matchPermissionScopes = permissionDescriptor.getPermissionScope().isPresent()
                && resourcePermission.getPermissionScope() == permissionDescriptor.getPermissionScope().get();
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
