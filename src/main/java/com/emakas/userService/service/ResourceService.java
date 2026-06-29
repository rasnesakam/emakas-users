package com.emakas.userService.service;

import com.emakas.userService.model.Resource;
import com.emakas.userService.repository.ResourceRepository;
import com.emakas.userService.shared.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResourceService extends CoreService<Resource, UUID>{
    private static final int SECRET_LENGTH = 256;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ResourceService(ResourceRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.resourceRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Resource> getByUri(String resourceUri) {
        return resourceRepository.findByUri(resourceUri);
    }

    public Collection<Resource> getResourcesByTenant(UUID tenantId) {
        return this.resourceRepository.findByTenant_Id(tenantId);
    }

    public String generateResourceSecretKey(Resource resource) {
        String secretString = StringUtils.generateSecretKey();
        String encodedSecretKey = passwordEncoder.encode(secretString);
        resource.setResourceSecret(encodedSecretKey);
        this.save(resource);
        return secretString;
    }
}
