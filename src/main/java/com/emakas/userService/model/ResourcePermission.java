package com.emakas.userService.model;

import com.emakas.userService.modelValidation.ResourcePermissionUTAValidation;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import com.emakas.userService.shared.enums.PermissionTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resource_permissions")
@ResourcePermissionUTAValidation
public class ResourcePermission extends BaseEntity{


    @JoinColumn(name = "resource_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Resource resource;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private User user;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Team team;

    @JoinColumn(name = "application_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Application application;

    @Column
    @Enumerated(EnumType.STRING)
    private PermissionTargetType permissionTargetType;

    @Column
    @Enumerated(EnumType.STRING)
    private PermissionScope permissionScope;

    @Column
    @Enumerated(EnumType.STRING)
    private AccessModifier accessModifier;

    /**
     * <h1>ResourcePermission.toString()</h1>
     * <p>String representation of resource permission</p>
     * <p>Format of string should be as follows</p>
     * <code>[permissionScope]:[accessModifier]:[resourceUri]</code>
     * @return String representation of resource permission
     * @author Ensar Makas
     */
    @Override
    public String toString() {
        return String.join(
                Constants.SEPARATOR,
                this.permissionScope.toString(),
                this.accessModifier.toString(),
                this.resource.getUri()
        );
    }
}
