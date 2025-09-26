package com.emakas.userService.controller;

import com.emakas.userService.dto.ApplicationDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.mappers.ApplicationDtoMapper;
import com.emakas.userService.model.Application;
import com.emakas.userService.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apps")
public class ApplicationsController {
    private final ApplicationService applicationService;
    private final ApplicationDtoMapper applicationDtoMapper;
    private final String appDomainName;
    @Autowired
    public ApplicationsController(ApplicationService applicationService, ApplicationDtoMapper applicationDtoMapper, @Value("${app.domain}") String appDomainName) {
        this.applicationService = applicationService;
        this.applicationDtoMapper = applicationDtoMapper;
        this.appDomainName = appDomainName;
    }

    @Operation(summary = "Register new application", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("register")
    @PreAuthorize("hasPermission(#RSC_APPS, 'self:write')")
    public ResponseEntity<Response<ApplicationDto>> createApplication(@RequestBody ApplicationDto applicationDto) {
        Application app = applicationDtoMapper.toApplication(applicationDto);
        app = applicationService.save(app);
        return ResponseEntity.ok(Response.of(applicationDtoMapper.toApplicationDto(app)));
    }

    @Operation(summary = "Generate secret key for app", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("generate-secret")
    @PreAuthorize("hasPermission(#RSC_APPS, 'global:write')")
    public ResponseEntity<Response<String>> generateSecretKey(@RequestParam(name = "client_id") UUID clientId){
        Optional<Application> appValue = applicationService.getById(clientId);
        return appValue.map(application -> {
            String clientSecret = applicationService.generateClientSecret(application);
            return ResponseEntity.ok(Response.of(clientSecret, "Client secret generated. Please store it somewhere safe"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.of("Applicaion not found.")));
    }

    @Operation(summary = "Delete existing application", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("delete")
    @PreAuthorize("hasPermission(#RSC_APPS, 'self:delete')")
    public ResponseEntity<ApplicationDto> deleteApplication(@RequestParam(name = "id") UUID appId) {
        Optional<Application> application = applicationService.getById(appId);
        return application.map(app -> ResponseEntity.ok(applicationDtoMapper.toApplicationDto(app)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get registered applications", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/")
    @PreAuthorize("hasPermission(#RSC_APPS, 'self:read')")
    public ResponseEntity<Collection<ApplicationDto>> getApplications() {
        Collection<ApplicationDto> applicationDtos = applicationService.getAll().stream().map(applicationDtoMapper::toApplicationDto).collect(Collectors.toSet());
        return ResponseEntity.ok(applicationDtos);
    }

    @GetMapping("info")
    public ResponseEntity<ApplicationDto> getApplicationInfo(@RequestParam(name="client_id") UUID clientId, @RequestParam(name = "redirect_uri") String redirectUri) {
        String decodedUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8);
        Optional<Application> applicationValue = applicationService.getById(clientId);
        return applicationValue.map(app -> {
            if (Objects.equals(app.getRedirectUri(), decodedUri))
                return ResponseEntity.ok(applicationDtoMapper.toApplicationDto(app));
            return ResponseEntity.notFound().<ApplicationDto>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get First Party Application", security = @SecurityRequirement(name = "X-CSRF-TOKEN"))
    @GetMapping("self")
    public ResponseEntity<ApplicationDto> getSelfApplication() {
        Optional<Application> application = applicationService.getByUri(appDomainName);
        return application.map(value -> ResponseEntity.ok(applicationDtoMapper.toApplicationDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
