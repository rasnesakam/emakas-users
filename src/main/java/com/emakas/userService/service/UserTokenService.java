package com.emakas.userService.service;

import com.emakas.userService.dto.IdTokenDto;
import com.emakas.userService.model.User;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class UserTokenService {
    private final UserService userService;
    private final TokenManager tokenManager;
    private final String issuer;
    @Autowired
    public UserTokenService(UserService userService, TokenManager tokenManager, @Value("${app.domain}") String issuer) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.issuer = issuer;
    }

    // Create Id Token for users

    private void fillInitialIdToken(IdTokenDto dto, User user, String audience) {
        dto.setIssuer(issuer);
        dto.setSubject(user.getId().toString());
        dto.setAudience(audience);
        dto.setAuthTime(Instant.now().getEpochSecond());
        dto.setIssuedAt(Instant.now().getEpochSecond());
        dto.setExpiresIn(Instant.now().plus(5, ChronoUnit.MINUTES).getEpochSecond());
    }

    private void fillProfileInformation(IdTokenDto dto,User user) {
        dto.setName(user.getFullName());
        dto.setGivenName(user.getName());
        dto.setFamilyName(user.getSurname());
        dto.setNickName(user.getUserName());
        dto.setPreferredUsername(user.getUserName());
        //dto.setPicture(""); //TODO: set up avatar later
        //dto.setGender(""); //TODO: set up gender later
        //dto.setBirthDate(null); //TODO: set up birth date later
        dto.setZoneInfo("Europe/Istanbul"); //TODO: You may need retrieve this from db
        dto.setLocale("tr-TR"); //TODO: You may need retrieve this from db
        dto.setUpdatedAt(user.getUpdatedTime().toInstant(ZoneOffset.UTC).getEpochSecond());
    }

    private void fillEmailInformation(IdTokenDto dto, User user) {
        dto.setEmail(user.getEmail());
        dto.setEmailVerified(true); //TODO: Set up email verification step
    }

    private Map<String, Object> generateClaimsFromDto(IdTokenDto dto) {
        Map<String, Object> claims = new TreeMap<>();

        claims.put("iss", dto.getIssuer());
        claims.put("sub", dto.getSubject());
        claims.put("aud", dto.getAudience());
        claims.put("iat", dto.getIssuedAt());
        claims.put("exp", dto.getExpiresIn());
        claims.put("auth_time", dto.getAuthTime());

        if (dto.getName() != null)
            claims.put("name", dto.getName());

        if (dto.getGivenName() != null)
            claims.put("given_name", dto.getGivenName());

        if (dto.getFamilyName() != null)
            claims.put("family_name", dto.getFamilyName());

        if (dto.getNickName() != null)
            claims.put("nickname", dto.getNickName());

        if (dto.getPreferredUsername() != null)
            claims.put("preferred_username", dto.getPreferredUsername());

        if (dto.getPicture() != null)
            claims.put("picture", dto.getPicture());

        if (dto.getGender() != null)
            claims.put("gender", dto.getGender());

        if (dto.getBirthDate() != null)
            claims.put("birthdate", dto.getBirthDate());

        if (dto.getZoneInfo() != null)
            claims.put("zoneinfo", dto.getZoneInfo());

        if (dto.getLocale() != null)
            claims.put("locale", dto.getLocale());

        if (dto.getUpdatedAt() != null)
            claims.put("updated_at", dto.getUpdatedAt());

        if (dto.getEmail() != null)
            claims.put("email", dto.getEmail());

        if (dto.getEmailVerified() != null)
            claims.put("email_verified", dto.getEmailVerified());

        return claims;
    }

    public String createIdToken(User user, String audience, Set<String> scopes) {
        IdTokenDto idTokenDto = new IdTokenDto();
        fillInitialIdToken(idTokenDto, user, audience);
        if (scopes.contains(Constants.SCOPE_PROFILE))
            fillProfileInformation(idTokenDto, user);
        if (scopes.contains(Constants.SCOPE_EMAIL))
            fillEmailInformation(idTokenDto, user);
        Map<String, Object> idTokenClaims = generateClaimsFromDto(idTokenDto);
       return tokenManager.generateCustomTokenWithClaims(idTokenClaims);
    }

}
