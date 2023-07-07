package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenManager implements Serializable {

    private String jwtSecret;
    private String issuer;
    private final Algorithm ALGORITHM;

    public TokenManager(@Value("${java-jwt.secret}") String jwtSecret, @Value("${java-jwt.issuer}")String issuer) {
        this.jwtSecret = jwtSecret;
        this.issuer = issuer;
        ALGORITHM = Algorithm.HMAC256(jwtSecret);
    }

    public String generateJwtToken(UserToken userToken) {
        Map<String, Object> claims = new HashMap<>();

        String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(userToken.getSub())
                .withAudience(userToken.getAud().split(" "))
                .withExpiresAt(Instant.parse(userToken.getExp()))
                .sign(ALGORITHM);
        return token;
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
                    .withAudience(audiences)
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
        token.setAud(String.join(" ",audiences));
        token.setExp(Instant.now().toString());
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }
}
