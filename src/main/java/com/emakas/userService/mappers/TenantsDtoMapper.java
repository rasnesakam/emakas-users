package com.emakas.userService.mappers;

import com.emakas.userService.dto.TenantDto;
import com.emakas.userService.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TenantsDtoMapper {
    static TenantsDtoMapper getInstance() { return Mappers.getMapper(TenantsDtoMapper.class); }

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    Tenant toTenant(TenantDto dto);

    TenantDto toTenantDto(Tenant tenant);
}
