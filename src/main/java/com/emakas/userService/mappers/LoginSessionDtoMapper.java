package com.emakas.userService.mappers;

import com.emakas.userService.dto.LoginSessionDto;
import com.emakas.userService.model.LoginSession;
import com.emakas.userService.service.LoginSessionService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class LoginSessionDtoMapper {
    public LoginSessionDtoMapper getInstance() { return Mappers.getMapper(LoginSessionDtoMapper.class);}

    @Mapping(target = "id", source = "sessionId")
    @Mapping(target = "requestedClient", ignore = true)
    @Mapping(target = "intendedUser", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "expireDate", ignore = true)
    public abstract LoginSession toLoginSession(LoginSessionDto dto, @Context LoginSessionService loginSessionService);

    @AfterMapping
    public void afterMappingToLoginSession(LoginSessionDto dto, @MappingTarget LoginSession session, @Context LoginSessionService loginSessionService) {
        Optional<LoginSession> loginSessionValue = loginSessionService.getById(dto.getSessionId());
        if (loginSessionValue.isPresent()) {
            LoginSession loginSession = loginSessionValue.get();
            session.setCreatedTime(loginSession.getCreatedTime());
            session.setUpdatedTime(loginSession.getUpdatedTime());
            session.setRequestedScopes(loginSession.getRequestedScopes());
            session.setRedirectUri(loginSession.getRedirectUri());
            session.setRequestedClient(loginSession.getRequestedClient());
            session.setIntendedUser(loginSession.getIntendedUser());
            session.setExpireDate(loginSession.getExpireDate());
            session.setId(loginSession.getId());
        }
    }

    @Mapping(target = "sessionId", source = "id")
    @Mapping(target = "clientId", source = "requestedClient.id")
    @Mapping(target = "audience", source = "requestedClient.uri")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "codeChallenge", ignore = true)
    public abstract LoginSessionDto toLoginSessionDto(LoginSession session);
}
