package com.emakas.userService.service;

import com.emakas.userService.model.Application;
import com.emakas.userService.repository.ApplicationRepository;
import com.emakas.userService.repository.CoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService extends CoreService<Application, UUID> {
    private final ApplicationRepository applicationRepository;
    @Autowired
    public ApplicationService(ApplicationRepository repository) {
        super(repository);
        this.applicationRepository = repository;
    }

    public Optional<Application> getByUri(String applicationUri) {
        return applicationRepository.findByUri(applicationUri);
    }
    public Optional<Application> getByRedirectUri(String redirectUri) {
        return applicationRepository.findByRedirectUri(redirectUri);
    }
}
