package com.emakas.userService.service;

import com.emakas.userService.model.*;
import com.emakas.userService.repository.CoreRepository;
import com.emakas.userService.repository.ResourcePermissionRepository;
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


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, AccessModifier modifier){
        return (resourcePermission.getAccessModifier() == modifier || resourcePermission.getAccessModifier() == AccessModifier.READ_WRITE)
                && resourcePermission.getResource().getUri().equals(targetDomainUri);
    }

    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, AccessModifier modifier, User user){
        return hasPermissionFor(resourcePermission, targetDomainUri, modifier)
                && resourcePermission.getPermissionTargetType() == PermissionTargetType.USER
                && resourcePermission.getUser().getId().equals(user.getId());
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, AccessModifier modifier, Team team){
        return hasPermissionFor(resourcePermission, targetDomainUri, modifier)
                && resourcePermission.getPermissionTargetType() == PermissionTargetType.TEAM
                && resourcePermission.getTeam().getId().equals(team.getId());
    }


    public boolean hasPermissionFor(ResourcePermission resourcePermission,  String targetDomainUri, AccessModifier modifier, Application application){
        return hasPermissionFor(resourcePermission, targetDomainUri, modifier)
                && resourcePermission.getPermissionTargetType() == PermissionTargetType.APP
                && resourcePermission.getApplication().getId().equals(application.getId());
    }
}
