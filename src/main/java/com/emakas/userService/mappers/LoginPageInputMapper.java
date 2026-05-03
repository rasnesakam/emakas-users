package com.emakas.userService.mappers;

import com.emakas.userService.dto.AuthorizeDto;
import com.emakas.userService.dto.LoginPageInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoginPageInputMapper {
    static LoginPageInputMapper getInstance() { return Mappers.getMapper(LoginPageInputMapper.class); }

    //@Mapping(target = "codeChallengeMethodString", source = "codeChallengeMethodString")
    LoginPageInput fromAuthorizeDto(AuthorizeDto authorizeDto);
}
