package com.emakas.userService.mappers;

import com.emakas.userService.dto.UserReadDto;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.model.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    static UserDtoMapper getInstance() {
        return Mappers.getMapper(UserDtoMapper.class);
    }

    UserReadDto userToUserReadDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fullName", expression = "java(userWriteDto.getName() + \" \" + userWriteDto.getSurname())")
    User updateUserFromUserWriteDto(UserWriteDto userWriteDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fullName", expression = "java(userWriteDto.getName() + \" \" + userWriteDto.getSurname())")
    User userFromUserWriteDto(UserWriteDto userWriteDto);


    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fullName", expression = "java(userReadDto.getName() + \" \" + userReadDto.getSurname())")
    User userFromUserReadDto(UserReadDto userReadDto);
}
