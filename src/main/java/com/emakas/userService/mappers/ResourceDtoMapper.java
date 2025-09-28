package com.emakas.userService.mappers;

import com.emakas.userService.dto.ResourceDto;
import com.emakas.userService.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResourceDtoMapper {
    static ResourceDtoMapper getInstance() {
        return Mappers.getMapper(ResourceDtoMapper.class);
    }

    @Mapping(target="createdTime", ignore = true)
    @Mapping(target="updatedTime", ignore = true)
    Resource toResource(ResourceDto resourceDto);

    ResourceDto toResourceDto(Resource resource);
}
