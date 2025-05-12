package com.emakas.userService.repository;

import com.emakas.userService.model.Resource;
import java.util.UUID;

public interface ResourceRepository extends CoreRepository<Resource, UUID> {
    Resource findByName(String name);
    Resource findByUri(String uri);
}
