package com.emakas.userService.controller;

import com.emakas.userService.dto.ResourceDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.mappers.ResourceMapper;
import com.emakas.userService.model.Resource;
import com.emakas.userService.model.User;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.ResourceService;
import com.emakas.userService.shared.SecurityContextManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {

    private final SecurityContextManager securityContextManager;
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;
    private final ResourceMapper resourceMapper;

    public ResourcesController(SecurityContextManager securityContextManager, ResourceService resourceService, ResourcePermissionService resourcePermissionService, ResourceMapper resourceMapper) {
        this.securityContextManager = securityContextManager;
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService;
        this.resourceMapper = resourceMapper;
    }

    @GetMapping("available")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'self:read')")
    public ResponseEntity<Collection<ResourceDto>> getAvailableResources() {
        Optional<User> userValue = securityContextManager.getCurrentUser();
        return userValue.map(user -> {
            Set<ResourceDto> resources = resourcePermissionService.getPermissionsByUser(user)
                    .stream()
                    .map(resourcePermission -> resourceMapper.toResourceDto(resourcePermission.getResource())).collect(Collectors.toSet());
            return ResponseEntity.<Collection<ResourceDto>>ok(resources);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("save")
    @PreAuthorize("hasPermission(#RSC_RESOURCES, 'self:write')")
    public ResponseEntity<Response<ResourceDto>> save(@RequestBody ResourceDto resourceDto) {
        Resource resource = resourceMapper.toResource(resourceDto);
        Resource savedResource = resourceService.save(resource);
        return ResponseEntity.ok(Response.of(resourceMapper.toResourceDto(savedResource)));
    }
}
