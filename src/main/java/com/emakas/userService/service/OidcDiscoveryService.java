package com.emakas.userService.service;

import com.emakas.userService.config.OidcConfig;
import com.emakas.userService.dto.JwksDto;
import com.emakas.userService.dto.JwksItemDto;
import com.emakas.userService.dto.OpenIdConfigDto;
import com.emakas.userService.model.Resource;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.shared.RsaKeyFactory;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class OidcDiscoveryService {

    private final OidcConfig oidcConfig;
    private final ResourceService resourceService;
    private final RsaKeyFactory rsaKeyFactory;

    public OidcDiscoveryService(OidcConfig oidcConfig, ResourceService resourceService, RsaKeyFactory rsaKeyFactory) {
        this.oidcConfig = oidcConfig;
        this.resourceService = resourceService;
        this.rsaKeyFactory = rsaKeyFactory;
    }

    public OpenIdConfigDto getConfigurationForTenant(String tenantId) {
        // Multi-tenancy standardı gereği dinamik issuer adresi oluşturuyoruz
        String dynamicIssuer = oidcConfig.getBaseUrl() + "/" + tenantId;

        OpenIdConfigDto dto = new OpenIdConfigDto();
        dto.setIssuer(dynamicIssuer);
        dto.setAuthorizationEndpoint(oidcConfig.getBaseUrl() + "/api/oauth2/authorize");
        dto.setTokenEndpoint(oidcConfig.getBaseUrl() + "/api/oauth/token");
        dto.setUserinfoEndpoint(oidcConfig.getBaseUrl() + "/api/user/profile");
        dto.setJwksUri(oidcConfig.getBaseUrl() + "/.well-known/jwks.json");

        // Scope'ları birleştir
        dto.setScopesSupported(calculateScopesForTenant(tenantId));

        // Config'den gelen diziler
        dto.setResponseTypesSupported(oidcConfig.getResponseTypesSupported().toArray(new String[0]));
        dto.setTokenEndpointAuthMethodsSupported(oidcConfig.getTokenEndpointAuthMethodsSupported().toArray(new String[0]));
        dto.setIdTokenSigningAlgValuesSupported(oidcConfig.getIdTokenSigningAlgValuesSupported().toArray(new String[0]));
        dto.setGrantTypesSupported(new String[]{"authorization_code", "client_credentials"});
        dto.setSubjectTypesSupported(oidcConfig.getSubjectTypesSupported().toArray(new String[0]));

        return dto;
    }

    public JwksDto buildJwks() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        RSAPublicKey publicKey = rsaKeyFactory.getPublicKey();

        JwksItemDto item = new JwksItemDto();
        item.setAlg("RS256");
        item.setKty("RSA");
        item.setUse("sig");
        item.setKid("key-2026-01");
        item.setE(base64Url(publicKey.getPublicExponent()));
        item.setN(base64Url(publicKey.getModulus()));

        JwksDto jwksDto = new JwksDto();
        jwksDto.setKeys(Collections.singletonList(item));
        return jwksDto;
    }

    private String[] calculateScopesForTenant(String tenantId) {
        Collection<Resource> resources = resourceService.getResourcesByTenant(UUID.fromString(tenantId));
        Stream<String> baseScopes = oidcConfig.getSupportedScopes().stream();
        Stream<String> dynamicScopes = resources.stream().flatMap(this::getScopesOfResource);

        return Stream.concat(baseScopes, dynamicScopes).toArray(String[]::new);
    }

    private Stream<String> getScopesOfResource(Resource resource) {
        return Arrays.stream(PermissionScope.values()).flatMap(ps ->
                Arrays.stream(AccessModifier.values()).map(am -> {
                    ResourcePermission resourcePermission = new ResourcePermission();
                    resourcePermission.setPermissionScope(ps);
                    resourcePermission.setAccessModifier(am);
                    resourcePermission.setResource(resource);
                    return resourcePermission.toString();
                })
        );
    }

    private String base64Url(BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
    }
}