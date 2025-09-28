package com.emakas.userService.service;

import com.emakas.userService.model.Resource;
import com.emakas.userService.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ResourceService extends CoreService<Resource, UUID>{

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository repository) {
        super(repository);
        this.resourceRepository = repository;
    }

    public Optional<Resource> getByUri(String resourceUri) {
        return resourceRepository.findByUri(resourceUri);
    }
}
