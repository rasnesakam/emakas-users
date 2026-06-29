package com.emakas.userService.controller;

import com.emakas.userService.dto.JwksDto;
import com.emakas.userService.dto.OpenIdConfigDto;
import com.emakas.userService.service.OidcDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/.well-known")
public class WellKnownController {

    private static final Logger log = LoggerFactory.getLogger(WellKnownController.class);
    private final OidcDiscoveryService oidcDiscoveryService;

    public WellKnownController(OidcDiscoveryService oidcDiscoveryService) {
        this.oidcDiscoveryService = oidcDiscoveryService;
    }

    @GetMapping("/{tenant-id}/openid-configuration")
    public ResponseEntity<OpenIdConfigDto> getOpenIdConfiguration(@PathVariable("tenant-id") String tenantId) {
        log.info("OIDC configuration requested for tenant: {}", tenantId);
        OpenIdConfigDto config = oidcDiscoveryService.getConfigurationForTenant(tenantId);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/jwks.json") // Baştaki slash standarda uygun hale getirildi
    public ResponseEntity<JwksDto> getJwks() {
        try {
            JwksDto jwks = oidcDiscoveryService.buildJwks();
            return ResponseEntity.ok(jwks);
        } catch (Exception e) {
            log.error("Failed to generate JWKS endpoint data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}