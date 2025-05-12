package com.emakas.userService.repository;

import com.emakas.userService.model.ResourcePermission;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface ResourcePermissionRepository extends CoreRepository<ResourcePermission, UUID> {

    Collection<ResourcePermission> findByUserId(UUID userId);
    Collection<ResourcePermission> findByUserIdAndResourceUri(UUID userId, String resourceUri);
    Collection<ResourcePermission> findByTeamId(UUID teamId);
    Collection<ResourcePermission> findByTeamIdAndResourceUri(UUID teamId, String resourceUri);
    Collection<ResourcePermission> findByApplicationId(UUID applicationId);
    Collection<ResourcePermission> findByApplicationIdAndResourceUri(UUID applicationId, String resourceUri);
}
