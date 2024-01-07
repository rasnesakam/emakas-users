package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenManager implements Serializable {

    @Value("${java-jwt.expiration}")
    private long secondsToExpire;
    private String jwtSecret;
    private String issuer;
    private final Algorithm ALGORITHM;

    public TokenManager(@Value("${java-jwt.secret}") String jwtSecret, @Value("${java-jwt.issuer}")String issuer) {
        this.jwtSecret = jwtSecret;
        this.issuer = issuer;
        ALGORITHM = Algorithm.HMAC256(jwtSecret);
    }

    public UserToken createUserToken(User user, long expireDateSecond, @Nullable String... audience){
        UserToken userToken = new UserToken();
        userToken.setIss(issuer);
        if (audience != null && audience.length > 0)
            userToken.setAud(String.join(",",audience));
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
                .withAudience(Optional.ofNullable(userToken.getAud()).orElse("").split(","))
                .withExpiresAt(Instant.ofEpochSecond(userToken.getExp()))
                .withIssuedAt(Instant.ofEpochSecond(userToken.getIat()))
                .sign(ALGORITHM);
    }

    public Optional<UserToken> getFromToken(@NonNull String token){
        try {
             DecodedJWT decodedJWT = JWT.decode(token);
             return Optional.of(new UserToken(
                     decodedJWT.getIssuer(),
                     String.join(",",decodedJWT.getAudience()),
                     decodedJWT.getSubject(),
                     decodedJWT.getExpiresAt().getTime(),token
             ));
        }
        catch (JWTDecodeException exception){
            return Optional.empty();
        }
    }

    /**
     * <h3>Verifies json that given in request</h3>
     * <p>Verifies token signature end expiration date</p>
     * @param jwtToken
     * @return true, if verification successful, false if verification is failed for some reason
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
            token.setAud(String.join(" ",audiences));
        token.setExp(Instant.now().plusSeconds(secondsToExpire).getEpochSecond());
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }
}
