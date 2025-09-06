package com.emakas.userService.dto;

import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import com.emakas.userService.shared.enums.PermissionTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePermissionDto {
    private ResourceDto resource;
    private UserReadDto user;
    private TeamReadDto team;
    private ApplicationDto application;
    private PermissionTargetType permissionTargetType;
    private PermissionScope permissionScope;
    private AccessModifier accessModifier;
}
