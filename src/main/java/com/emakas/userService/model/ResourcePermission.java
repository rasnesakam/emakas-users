package com.emakas.userService.model;

import com.emakas.userService.modelValidation.ResourcePermissionUTAValidation;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.security.Permission;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resource_permissions")
@ResourcePermissionUTAValidation
public class ResourcePermission extends BaseEntity{

    @Column
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "resource_permission_resources", joinColumns = @JoinColumn(name = "resource_id"))
    private Resource resource;

    @Column
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "resource_permission_users", joinColumns = @JoinColumn(name = "user_id"))
    private User user;

    @Column
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "resource_permission_teams", joinColumns = @JoinColumn(name = "team_id"))
    private Team team;

    @Column
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CollectionTable(name = "resource_permission_apps", joinColumns = @JoinColumn(name = "app_id"))
    private Application application;

    @Column
    private PermissionTargetType permissionTargetType;

    @Column
    private AccessModifier accessModifier;
}
