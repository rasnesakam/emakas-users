package com.emakas.userService.mappers;

import com.emakas.userService.dto.ResourcePermissionDto;
import com.emakas.userService.model.ResourcePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ApplicationDtoMapper.class, UserDtoMapper.class, ResourceDtoMapper.class})
public abstract class ResourcePermissionDtoMapper {
    static ResourcePermissionDtoMapper getInstance() {
        return Mappers.getMapper(ResourcePermissionDtoMapper.class);
    }

    public abstract ResourcePermissionDto toResourcePermissionDto(ResourcePermission resourcePermission);


    @Mapping(target="id", ignore = true)
    @Mapping(target="createdTime", ignore = true)
    @Mapping(target="updatedTime", ignore = true)
    @Mapping(target="team", ignore = true)
    public abstract ResourcePermission toResourcePermission(ResourcePermissionDto resourcePermissionDto);
}
