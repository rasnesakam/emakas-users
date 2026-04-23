package com.emakas.userService.mappers;

import com.emakas.userService.domain.auth.ClientCredential;
import com.emakas.userService.domain.auth.ClientPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientPrincipalMapper {
    static ClientPrincipalMapper getInstance() { return Mappers.getMapper(ClientPrincipalMapper.class); }

    ClientPrincipal fromClientCredential(ClientCredential clientCredential);
}
