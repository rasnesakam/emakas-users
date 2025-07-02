package com.emakas.userService.mappers;

import com.emakas.userService.dto.ApplicationDto;
import com.emakas.userService.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationDtoMapper {
    static ApplicationDtoMapper getInstance() {return Mappers.getMapper(ApplicationDtoMapper.class);}

    ApplicationDto toApplicationDto(Application application);
    Application toApplication(ApplicationDto applicationDto);
}
