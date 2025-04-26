package com.emakas.userService.model;

import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionTargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.security.Permission;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resource_permissions")
public class ResourcePermission extends BaseEntity{

    private Resource resource;
    private AccessModifier accessModifier;
    private User user;
    private Team team;
    private PermissionTargetType permissionTargetType;
}
