package com.emakas.userService.mappers;

import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.model.Token;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TokenIntrospectionMapper {
    static TokenIntrospectionDto getInstance() { return Mappers.getMapper(TokenIntrospectionDto.class); }

    @Mapping(target = "active", ignore = true) // şimdilik ignore
    @Mapping(target = "username", ignore = true) // şimdilik ignore
    @Mapping(target = "sub", source = "sub")
    @Mapping(target = "exp", source = "exp")
    @Mapping(target = "iat", source = "iat")
    @Mapping(target = "scope", expression = "java(String.join(\" \", token.getScope()))")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "tokenType", source = "tokenType")
    @Mapping(target = "aud", expression = "java(String.join(\" \", token.getAud()))")
    @Mapping(target = "iss", source = "iss")
    TokenIntrospectionDto toIntrospection(Token token);
}
