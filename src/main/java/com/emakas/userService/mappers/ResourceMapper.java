package com.emakas.userService.mappers;

import com.emakas.userService.dto.ResourceDto;
import com.emakas.userService.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    static ResourceMapper getInstance() {
        return Mappers.getMapper(ResourceMapper.class);
    }

    Resource toResource(ResourceDto resourceDto);

    ResourceDto toResourceDto(Resource resource);
}
