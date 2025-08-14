package com.emakas.userService.mappers;

import com.emakas.userService.dto.ApplicationDto;
import com.emakas.userService.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationDtoMapper {
    static ApplicationDtoMapper getInstance() {return Mappers.getMapper(ApplicationDtoMapper.class);}

    @Mapping(target = "clientId", source = "id")
    ApplicationDto toApplicationDto(Application application);

    @Mapping(target="id", ignore = true)
    @Mapping(target="createdTime", ignore = true)
    @Mapping(target="updatedTime", ignore = true)
    Application toApplication(ApplicationDto applicationDto);
}
