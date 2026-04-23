package com.emakas.userService.mappers;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPrincipalMapper {
    static UserPrincipalMapper getInstance() {
        return Mappers.getMapper(UserPrincipalMapper.class);
    }

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "userId", expression = "java(java.util.UUID.fromString(token.getSub()))")
    @Mapping(target = "authorities", source = "scope")
    @Mapping(target = "tokenType", source = "tokenType")
    public UserPrincipal fromUserToken(Token token);

    @Mapping(target = "username", source = "userName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "tokenType", ignore = true)
    public UserPrincipal fromUser(User user);
}
