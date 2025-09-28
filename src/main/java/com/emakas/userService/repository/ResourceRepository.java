package com.emakas.userService.repository;

import com.emakas.userService.model.Resource;

import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository extends CoreRepository<Resource, UUID> {
    Optional<Resource> findByName(String name);
    Optional<Resource> findByUri(String uri);
}
