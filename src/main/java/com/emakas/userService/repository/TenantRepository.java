package com.emakas.userService.repository;

import com.emakas.userService.model.Tenant;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRepository extends CoreRepository<Tenant, UUID> {
}
