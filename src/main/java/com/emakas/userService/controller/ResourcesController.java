package com.emakas.userService.controller;

import com.emakas.userService.dto.ResourceDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.mappers.ResourceDtoMapper;
import com.emakas.userService.model.Resource;
import com.emakas.userService.model.User;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.ResourceService;
import com.emakas.userService.shared.SecurityContextManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {

    private final SecurityContextManager securityContextManager;
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;
    private final ResourceDtoMapper resourceDtoMapper;

    public ResourcesController(SecurityContextManager securityContextManager, ResourceService resourceService, ResourcePermissionService resourcePermissionService, ResourceDtoMapper resourceDtoMapper) {
        this.securityContextManager = securityContextManager;
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService;
        this.resourceDtoMapper = resourceDtoMapper;
    }

    @GetMapping("available")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'self:read')")
    public ResponseEntity<Collection<ResourceDto>> getAvailableResources() {
        Optional<User> userValue = securityContextManager.getCurrentUser();
        return userValue.map(user -> {
            Set<ResourceDto> resources = resourcePermissionService.getPermissionsByUser(user)
                    .stream()
                    .map(resourcePermission -> resourceDtoMapper.toResourceDto(resourcePermission.getResource())).collect(Collectors.toSet());
            return ResponseEntity.<Collection<ResourceDto>>ok(resources);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("all")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'global:read')")
    public ResponseEntity<Collection<ResourceDto>> getAllResources(){
        return ResponseEntity.ok(resourceService.getAll().parallelStream().map(resourceDtoMapper::toResourceDto).collect(Collectors.toList()));
    }

    @PostMapping("save")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'self:write')")
    public ResponseEntity<Response<ResourceDto>> save(@RequestBody ResourceDto resourceDto) {
        Resource resource = resourceDtoMapper.toResource(resourceDto);
        Resource savedResource = resourceService.save(resource);
        return ResponseEntity.ok(Response.of(resourceDtoMapper.toResourceDto(savedResource)));
    }

    @DeleteMapping("delete")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'global:write')")
    public ResponseEntity<Response<ResourceDto>> deleteResource(@RequestParam(name = "resource_id") UUID resourceId) {
        return resourceService.getById(resourceId).map(resource -> {
            resourceService.delete(resource);
            return ResponseEntity.ok(Response.of(resourceDtoMapper.toResourceDto(resource), "Resource deleted successfully"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.of("Resource not found")));
    }

    @PostMapping("generate-secret")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'global:write')")
    public ResponseEntity<Response<String>> generateResourceSecret(@RequestParam(name = "resource_id") UUID resourceId) {
        return resourceService.getById(resourceId).map(res -> {
            String resourceSecret = resourceService.generateResourceSecretKey(res);
            return ResponseEntity.ok(Response.of(resourceSecret, "Secret key generated"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.of("Application did not found")));
    }
}
