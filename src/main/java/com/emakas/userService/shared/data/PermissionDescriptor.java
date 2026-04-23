package com.emakas.userService.shared.data;

import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
public class PermissionDescriptor {
    private Optional<AccessModifier> accessModifier;
    private Optional<PermissionScope> permissionScope;
}
