package com.emakas.userService.mappers;

import com.emakas.userService.dto.LoginSessionDto;
import com.emakas.userService.model.LoginSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoginSessionDtoMapper {
    static LoginSessionDtoMapper getInstance() { return Mappers.getMapper(LoginSessionDtoMapper.class);}

    LoginSession toLoginSession(LoginSessionDto dto);

    @Mapping(target = "sessionId", source = "id")
    @Mapping(target = "clientId", source = "requestedClient.id")
    @Mapping(target = "audience", source = "requestedClient.uri")
    LoginSessionDto toLoginSessionDto(LoginSession session);
}
