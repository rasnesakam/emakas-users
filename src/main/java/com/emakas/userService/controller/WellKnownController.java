package com.emakas.userService.controller;

import com.emakas.userService.config.OidcConfig;
import com.emakas.userService.dto.JwksDto;
import com.emakas.userService.dto.JwksItemDto;
import com.emakas.userService.dto.OpenIdConfigDto;
import com.emakas.userService.shared.RsaKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/.well-known")
public class WellKnownController {


    private static final Logger log = LoggerFactory.getLogger(WellKnownController.class);
    private final String issuer;
    private final String authorizationUrl;
    private final String tokenEndpoint;
    private final String userInfoEndpoint;
    private final String jwksEndpoint;
    private final String[] scopesSupported;
    private final String[] responseTypesSupported;
    private final String[] tokenEndpointAuthMethodsSupported;
    private final RsaKeyFactory rsaKeyFactory;

    public WellKnownController(@Value("${java-jwt.issuer}") String issuer, OidcConfig oidcConfig, RsaKeyFactory rsaKeyFactory) {

        String baseUrl = "https://" + issuer;
        this.issuer = baseUrl;
        this.authorizationUrl = baseUrl + "/api/oauth2/authorize";
        this.tokenEndpoint = baseUrl + "/api/oauth/token";
        this.userInfoEndpoint = baseUrl + "/api/user/profile";
        this.jwksEndpoint = baseUrl + "/.well-known/jwks.json";
        this.scopesSupported = oidcConfig.getSupportedScopes().toArray(new String[0]);
        this.responseTypesSupported = oidcConfig.getResponseTypesSupported().toArray(new String[0]);
        this.tokenEndpointAuthMethodsSupported = oidcConfig.getTokenEndpointAuthMethodsSupported().toArray(new String[0]);
        this.rsaKeyFactory = rsaKeyFactory;
    }


    @GetMapping("openid-configuration")
    public ResponseEntity<OpenIdConfigDto> getOpenIdConfiguration() {
        OpenIdConfigDto openIdConfigDto = new OpenIdConfigDto();
        openIdConfigDto.setIssuer(this.issuer);
        openIdConfigDto.setAuthorizationEndpoint(this.authorizationUrl);
        openIdConfigDto.setTokenEndpoint(this.tokenEndpoint);
        openIdConfigDto.setJwksUri(this.jwksEndpoint);
        openIdConfigDto.setScopesSupported(this.scopesSupported);
        openIdConfigDto.setResponseTypesSupported(this.responseTypesSupported);
        openIdConfigDto.setTokenEndpointAuthMethodsSupported(this.tokenEndpointAuthMethodsSupported);
        openIdConfigDto.setUserinfoEndpoint(this.userInfoEndpoint);
        return ResponseEntity.ok(new OpenIdConfigDto());
    }


    @GetMapping("jwks.json")
    public ResponseEntity<JwksDto> getJwks() {
        //TODO: Finish implementing of jwks endpoint
        JwksDto jwksConfig = new JwksDto();
        try {
            RSAPublicKey publicKey = rsaKeyFactory.getPublicKey();

            JwksItemDto jwksItemDto = new JwksItemDto();
            jwksItemDto.setAlg("RS256");
            jwksItemDto.setKty("RSA");
            jwksItemDto.setUse("sig");
            jwksItemDto.setKid("key-2026-01");
            jwksItemDto.setE(base64Url(publicKey.getPublicExponent()));
            jwksItemDto.setN(base64Url(publicKey.getModulus()));

            jwksConfig.setKeys(Stream.of(jwksItemDto).collect(Collectors.toList()));
            return ResponseEntity.ok(jwksConfig);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    private String base64Url(BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.toByteArray());
    }
}
