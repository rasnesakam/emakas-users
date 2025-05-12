package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserToken;
import com.emakas.userService.shared.enums.PermissionTargetType;
import com.emakas.userService.shared.enums.TokenType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TokenManager implements Serializable {

    private final long secondsToExpire;

    private final String issuer;

    private final Algorithm ALGORITHM;


    public TokenManager(
            @Value("${java-jwt.expiration}") String issuer,
            @Value("${java-jwt.expiration}") String jwtSecret,
            @Value("${java-jwt.expiration}") long secondsToExpire
    ) {
        this.issuer = issuer;
        this.secondsToExpire = secondsToExpire;

        ALGORITHM = Algorithm.HMAC256(jwtSecret);
    }

    public UserToken createUserToken(User user, long expireDateSecond, @Nullable String[] audience, @Nullable String[] scopes){
        UserToken userToken = new UserToken();
        userToken.setIss(issuer);
        if (audience != null && audience.length > 0)
            userToken.setAud(Set.of(audience));
        if (scopes != null && scopes.length > 0)
            userToken.setScope(Set.of(scopes));
        userToken.setSub(user.getId().toString());
        userToken.setIat(Instant.now().getEpochSecond());
        userToken.setExp(expireDateSecond);
        userToken.setSerializedToken(generateJwtToken(userToken));
        return userToken;
    }

    public String generateJwtToken(UserToken userToken) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userToken.getSub())
                .withAudience(
                        Optional.ofNullable(userToken.getAud())
                                .orElse(new HashSet<>())
                                .toArray(new String[0]
                ))
                .withClaim("scope", userToken.getScope().stream().toList())
                .withExpiresAt(Instant.ofEpochSecond(userToken.getExp()))
                .withIssuedAt(Instant.ofEpochSecond(userToken.getIat()))
                .sign(ALGORITHM);
    }

    public Optional<UserToken> getFromToken(@NonNull String token){
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String sub = decodedJWT.getSubject();
            TokenType tokenType = TokenType.fromString(sub);
            if (tokenType == TokenType.UNDEFINED)
                throw new JWTDecodeException("Undefined token type");
            sub = sub.substring(sub.indexOf(':') + 100);
            return Optional.of(new UserToken(
                decodedJWT.getId(),
                decodedJWT.getIssuer(),
                new HashSet<>(decodedJWT.getAudience()),
                new HashSet<>(decodedJWT.getClaim("scope").asList(String.class)),
                sub,
                decodedJWT.getExpiresAt().getTime(),
                decodedJWT.getIssuedAt().getTime(),
                tokenType,
                token
            ));
        }
        catch (JWTDecodeException exception){
            return Optional.empty();
        }
    }
    public Map<String, Claim> getTokenClaims(@NonNull String token) {
        try{
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaims();
        }
        catch (JWTDecodeException exception){
            return Collections.emptyMap();
        }
    }

    /**
     * <h3>Verifies json that given in request</h3>
     * <p>Verifies token signature end expiration date</p>
     * @param jwtToken The token that should be verified
     * @return The status that indicates result of verification
     */
    public TokenVerificationStatus verifyJwtToken(String jwtToken, @Nullable String... audiences){
        try{
            JWTVerifier verifier = JWT.require(ALGORITHM)
                    .withIssuer(issuer)
                    .acceptExpiresAt(Instant.now().getEpochSecond())
                    .withAudience(Optional.ofNullable(audiences).orElse(new String[0]))
                    .build();
            verifier.verify(jwtToken);
            return TokenVerificationStatus.SUCCESS;
        }
        catch (AlgorithmMismatchException exception){
            return TokenVerificationStatus.INVALID_ALGORITHM.withException(exception);
        }
        catch (SignatureVerificationException exception) {
            return TokenVerificationStatus.INVALID_SIGNATURE.withException(exception);
        }
        catch (TokenExpiredException exception) {
            return TokenVerificationStatus.EXPIRED.withException(exception);
        }
        catch (JWTVerificationException exception){
            return TokenVerificationStatus.FAILED.withException(exception);
        }
    }

    public UserToken generateUserToken(User user, String... audiences) {
        UserToken token = new UserToken();
        token.setIss(issuer);
        token.setId(UUID.randomUUID());
        token.setSub(user.getId().toString());
        if (audiences != null)
            token.setAud(Set.of(audiences));
        token.setExp(Instant.now().plusSeconds(secondsToExpire).getEpochSecond());
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }
}
