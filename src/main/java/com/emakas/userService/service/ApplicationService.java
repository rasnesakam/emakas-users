package com.emakas.userService.service;

import com.emakas.userService.model.Application;
import com.emakas.userService.repository.ApplicationRepository;
import com.emakas.userService.repository.CoreRepository;
import com.emakas.userService.shared.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService extends CoreService<Application, UUID> {
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public ApplicationService(ApplicationRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.applicationRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Application> getByUri(String applicationUri) {
        return applicationRepository.findByUri(applicationUri);
    }
    public Optional<Application> getByRedirectUri(String redirectUri) {
        return applicationRepository.findByRedirectUri(redirectUri);
    }

    public String generateClientSecret(Application application) {
        String randomString = StringUtils.getRandomString(64);
        String encodedRandomString = ""; // passwordEncoder.encode(randomString);
        application.setClientSecret(encodedRandomString);
        applicationRepository.save(application);
        return encodedRandomString;
    }
}
