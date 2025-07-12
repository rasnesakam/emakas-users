package com.emakas.userService.repository;

import com.emakas.userService.model.Application;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends CoreRepository<Application, UUID> {

    Optional<Application> findByUri(String uri);

    Optional<Application> findByRedirectUri(String redirectUri);
    
}
