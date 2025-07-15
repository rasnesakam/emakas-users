package com.emakas.userService.controller;

import com.emakas.userService.dto.ApplicationDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.mappers.ApplicationDtoMapper;
import com.emakas.userService.model.Application;
import com.emakas.userService.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    @PostMapping("/register")
    public ResponseEntity<Response<ApplicationDto>> createApplication(@RequestBody ApplicationDto applicationDto) {
        Application app = applicationDtoMapper.toApplication(applicationDto);
        app = applicationService.save(app);
        return ResponseEntity.ok(Response.of(applicationDtoMapper.toApplicationDto(app)));
    }

    @GetMapping("/")
    public ResponseEntity<Collection<ApplicationDto>> getApplications() {
        Collection<ApplicationDto> applicationDtos = applicationService.getAll().stream().map(applicationDtoMapper::toApplicationDto).collect(Collectors.toSet());
        return ResponseEntity.ok(applicationDtos);
    }

    @GetMapping("/self")
    public ResponseEntity<ApplicationDto> getSelfApplication() {
        Optional<Application> application = applicationService.getByUri(appDomainName);
        return application.map(value -> ResponseEntity.ok(applicationDtoMapper.toApplicationDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
