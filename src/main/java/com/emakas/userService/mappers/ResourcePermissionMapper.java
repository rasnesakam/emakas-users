package com.emakas.userService.mappers;

import com.emakas.userService.dto.ResourcePermissionDto;
import com.emakas.userService.model.ResourcePermission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResourcePermissionMapper {
    static ResourcePermissionMapper getInstance() {
        return Mappers.getMapper(ResourcePermissionMapper.class);
    }

    ResourcePermissionDto toResourcePermissionDto(ResourcePermission resourcePermission);
    ResourcePermission toResourcePermission(ResourcePermissionDto resourcePermissionDto);
}
