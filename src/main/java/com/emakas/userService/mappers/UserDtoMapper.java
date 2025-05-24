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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromUserWriteDto(UserWriteDto userWriteDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    User UserFromUserWriteDto(UserWriteDto userWriteDto);
}
