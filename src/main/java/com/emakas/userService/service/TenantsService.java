package com.emakas.userService.service;

import com.emakas.userService.model.Tenant;
import com.emakas.userService.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TenantsService extends CoreService<Tenant, UUID> {
    public TenantsService(TenantRepository tenantRepository) {
        super(tenantRepository);
    }

}
