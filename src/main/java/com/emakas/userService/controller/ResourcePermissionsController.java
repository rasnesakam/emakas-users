package com.emakas.userService.controller;

import com.emakas.userService.cmdRunner.InitializeRootVariables;
import com.emakas.userService.dto.ResourcePermissionDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.mappers.ResourcePermissionDtoMapper;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.service.ResourcePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/resource-permissions")
public class ResourcePermissionsController {

    private final Logger logger = LoggerFactory.getLogger(InitializeRootVariables .class);

    private final ResourcePermissionService resourcePermissionService;
    private final ResourcePermissionDtoMapper mapper;

    public ResourcePermissionsController(ResourcePermissionService resourcePermissionService, ResourcePermissionDtoMapper mapper){
        this.resourcePermissionService = resourcePermissionService;
        this.mapper = mapper;
    }

    @Operation(summary = "Assign New Resource Permission", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("assign")
    @PreAuthorize("hasPermission(#RSC_PERMISSIONS, 'global:write')")
    public ResponseEntity<Response<ResourcePermissionDto>> registerPermission(@RequestBody ResourcePermissionDto resourcePermissionDto) {
        ResourcePermission resourcePermission = mapper.toResourcePermission(resourcePermissionDto);
        try {
            Optional<ResourcePermission> newResourcePermission = resourcePermissionService.registerResourcePermission(resourcePermission);
            if (newResourcePermission.isEmpty())
                return ResponseEntity.badRequest().body(Response.of("Could not create permission."));
            URI resourceUri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(resourcePermission.getId())
                    .toUri();
            return ResponseEntity.created(resourceUri).body(Response.of(mapper.toResourcePermissionDto(resourcePermission)));
        }
        catch (IllegalArgumentException illegalArgumentException){
            logger.error(illegalArgumentException.getLocalizedMessage());
            return ResponseEntity.badRequest().body(Response.of(illegalArgumentException.getMessage()));
        }
        catch (OptimisticLockingFailureException optimisticLockingFailureException){
            logger.error(optimisticLockingFailureException.getLocalizedMessage());
            return ResponseEntity.internalServerError().body(Response.of(optimisticLockingFailureException.getMessage()));
        }
    }

    @Operation(summary = "Get All resource permissions", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("all")
    @PreAuthorize("hasPermission(#RSC_PERMISSIONS, 'global:read')")
    public ResponseEntity<Collection<ResourcePermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(resourcePermissionService.getAll().parallelStream().map(mapper::toResourcePermissionDto).toList());
    }
}
