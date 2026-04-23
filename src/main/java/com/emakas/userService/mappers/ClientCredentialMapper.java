package com.emakas.userService.mappers;

import com.emakas.userService.domain.auth.ClientCredential;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientCredentialMapper {
    static ClientCredentialMapper getInstance() { return Mappers.getMapper(ClientCredentialMapper.class); }

    @Mapping(target = "clientName", source = "name")
    @Mapping(target = "clientId", source = "id")
    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "clientType", expression = "java(com.emakas.userService.domain.auth.ClientType.APPLICATION)")
    ClientCredential fromApplication(Application application);

    @Mapping(target = "clientName", source = "name")
    @Mapping(target = "clientId", source = "id")
    @Mapping(target = "clientSecret", source = "resourceSecret")
    @Mapping(target = "clientType", expression = "java(com.emakas.userService.domain.auth.ClientType.RESOURCE)")
    ClientCredential fromResource(Resource resource);
}
