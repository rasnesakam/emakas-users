package com.emakas.userService.shared;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.emakas.userService.model.AccessToken;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.IdToken;
import com.emakas.userService.model.RefreshToken;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserLogin;
import com.emakas.userService.shared.enums.PrincipalType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenManager implements Serializable {

    private final long accessTokenExpiresAt;
    private final int refreshTokenExpiresAt;
    private final long idTokenExpiresAt;
    private final String issuer;
    private final Algorithm ALGORITHM;

    public TokenManager(
            @Value("${app.domain}") String issuer,
            @Value("${java-jwt.secret}") String jwtSecret,
            @Value("${java.jwt.access_expiration}") long accessTokenExpiresAt,
            @Value("${java-jwt.refresh_expiration}") int refreshTokenExpiresAt,
            @Value("${java-jwt.id_expiration}") long idTokenExpiresAt,
            RsaKeyFactory rsaKeyFactory) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.issuer = issuer;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.idTokenExpiresAt = idTokenExpiresAt;

        RSAPrivateKey privateKey = rsaKeyFactory.getPrivateKey();
        RSAPublicKey publicKey = rsaKeyFactory.getPublicKey();
        this.ALGORITHM = Algorithm.RSA256(publicKey, privateKey);
    }

    private void setupBaseToken(Token token, long expiresAt) {
        token.setIss(issuer);
        token.setIat(Instant.now().getEpochSecond());
        token.setExp(expiresAt);
    }

    private AccessToken createAccessToken(UUID subjectId, PrincipalType principalType, Application client,
            String[] audiences, String[] scopes) {
        AccessToken accessToken = new AccessToken();
        setupBaseToken(accessToken, Instant.now().plus(accessTokenExpiresAt, ChronoUnit.SECONDS).getEpochSecond());
        accessToken.setSub(subjectId.toString());
        accessToken.setPrincipalType(principalType);
        accessToken.setClient(client);
        if (audiences != null && audiences.length > 0)
            accessToken.setAud(Set.of(audiences));
        if (scopes != null && scopes.length > 0)
            accessToken.setScope(Set.of(scopes));
        return accessToken;
    }

    public AccessToken createAccessToken(User user, Application client, String[] audiences, String[] scopes) {
        return createAccessToken(user.getId(), PrincipalType.USER, client, audiences, scopes);
    }

    public AccessToken createAccessToken(Application application, String[] audiences, String[] scopes) {
        return createAccessToken(application.getId(), PrincipalType.APP, application, audiences, scopes);
    }

    // TODO: Should I use UserLogin or LoginSession?
    public RefreshToken createRefreshToken(UserLogin userLogin) {
        RefreshToken refreshToken = new RefreshToken();
        setupBaseToken(refreshToken, Instant.now().plus(refreshTokenExpiresAt, ChronoUnit.DAYS).getEpochSecond());
        // TODO: fill out missing fields
        return refreshToken;
    }

    public IdToken createIdToken(User user) {
        IdToken idToken = new IdToken();
        setupBaseToken(idToken, Instant.now().plus(idTokenExpiresAt, ChronoUnit.SECONDS).getEpochSecond());
        // TODO: fill out missing fields
        return idToken;
    }

    public String createCustomToken(Map<String, String> additionalClaims) {
        Token token = new Token();
        setupBaseToken(token, Instant.now().plus(accessTokenExpiresAt, ChronoUnit.SECONDS).getEpochSecond());
        JWTCreator.Builder builder = getTokenBuilder(token);
        additionalClaims.forEach(builder::withClaim);
        return builder.sign(ALGORITHM);
    }

    private JWTCreator.Builder getTokenBuilder(Token token) {
        return JWT.create()
                .withIssuer(token.getIss())
                .withExpiresAt(Instant.ofEpochSecond(token.getExp()))
                .withJWTId(token.getJti().toString())
                .withIssuedAt(Instant.ofEpochSecond(token.getIat()));
    }

    public String serializeAccessToken(AccessToken accessToken) {
        JWTCreator.Builder builder = getTokenBuilder(accessToken);
        builder.withAudience(accessToken.getAud().toArray(String[]::new));
        builder.withClaim(Constants.JwtClaims.AccessTokenClaims.SCOPE, accessToken.getScope().stream().toList());
        builder.withClaim(Constants.JwtClaims.AccessTokenClaims.PRINCIPAL_TYPE,
                accessToken.getPrincipalType().toString());
        builder.withClaim(Constants.JwtClaims.AccessTokenClaims.CLIENT_ID, accessToken.getClient().getId().toString());
        return builder.sign(ALGORITHM);
    }

    public String serializeRefreshToken(RefreshToken refreshToken) {
        JWTCreator.Builder builder = getTokenBuilder(refreshToken);
        builder.withClaim(Constants.JwtClaims.RefreshTokenClaims.USER_LOGIN,
                refreshToken.getUserLogin().getId().toString());
        return builder.sign(ALGORITHM);
    }

    public String serializeIdToken(IdToken idToken) {
        JWTCreator.Builder builder = getTokenBuilder(idToken);
        // TODO: implement the fields you added here.
        return builder.sign(ALGORITHM);
    }

    private void parseBaseClaims(Token token, DecodedJWT decodedJWT) {
        token.setJti(UUID.fromString(decodedJWT.getId()));
        token.setIss(decodedJWT.getIssuer());
        token.setExp(decodedJWT.getExpiresAtAsInstant().getEpochSecond());
        token.setIat(decodedJWT.getIssuedAtAsInstant().getEpochSecond());
    }

    public AccessToken parseAccessToken(String token) {
        AccessToken accessToken = new AccessToken();
        DecodedJWT decodedJWT = JWT.decode(token);

        parseBaseClaims(accessToken, decodedJWT);

        if (!decodedJWT.getClaims().containsKey(Constants.JwtClaims.AccessTokenClaims.PRINCIPAL_TYPE))
            throw new IllegalArgumentException(
                    String.format("%s is not set for token", Constants.JwtClaims.AccessTokenClaims.PRINCIPAL_TYPE));
        String principalTypeClaimString = decodedJWT.getClaim(Constants.JwtClaims.AccessTokenClaims.PRINCIPAL_TYPE)
                .asString();
        PrincipalType principalType = EnumUtils.parseOrDefault(PrincipalType.class, principalTypeClaimString,
                PrincipalType.UNDEFINED);
        accessToken.setPrincipalType(principalType);

        if (StringUtils.isNullOrEmpty(decodedJWT.getSubject()))
            throw new IllegalArgumentException("sub claim is not set for token");
        accessToken.setSub(decodedJWT.getSubject());

        if (decodedJWT.getAudience() == null || decodedJWT.getAudience().isEmpty())
            throw new IllegalArgumentException("aud claim is not set for token");
        accessToken.setAud(new HashSet<>(decodedJWT.getAudience()));

        if (!decodedJWT.getClaims().containsKey(Constants.JwtClaims.AccessTokenClaims.SCOPE))
            throw new IllegalArgumentException(
                    String.format("%s is not set for token", Constants.JwtClaims.AccessTokenClaims.SCOPE));
        accessToken.setScope(
                new HashSet<>(decodedJWT.getClaim(Constants.JwtClaims.AccessTokenClaims.SCOPE).asList(String.class)));

        if (!decodedJWT.getClaims().containsKey(Constants.JwtClaims.AccessTokenClaims.CLIENT_ID))
            throw new IllegalArgumentException(
                    String.format("%s is not set for token", Constants.JwtClaims.AccessTokenClaims.CLIENT_ID));
        String clientIdString = decodedJWT.getClaim(Constants.JwtClaims.AccessTokenClaims.CLIENT_ID).asString();
        if (!StringUtils.isNullOrEmpty(clientIdString)) {
            Application application = new Application();
            application.setId(UUID.fromString(clientIdString));
            accessToken.setClient(application);
        }
        return accessToken;
    }

    public RefreshToken parseRefreshToken(String token) {
        RefreshToken refreshToken = new RefreshToken();
        DecodedJWT decodedJWT = JWT.decode(token);

        parseBaseClaims(refreshToken, decodedJWT);
        if (!decodedJWT.getClaims().containsKey(Constants.JwtClaims.RefreshTokenClaims.USER_LOGIN))
            throw new IllegalArgumentException(
                    String.format("%s is not set for token", Constants.JwtClaims.RefreshTokenClaims.USER_LOGIN));
        UserLogin userLogin = new UserLogin();
        userLogin.setId(
                UUID.fromString(decodedJWT.getClaim(Constants.JwtClaims.RefreshTokenClaims.USER_LOGIN).asString()));
        refreshToken.setUserLogin(userLogin);
        return refreshToken;
    }

    public Map<String, Claim> getTokenClaims(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaims();
        } catch (JWTDecodeException exception) {
            return Collections.emptyMap();
        }
    }

    public <T> Optional<T> getTokenClaim(String token, String claim, Class<T> clazz) {
        Map<String, Claim> claims = getTokenClaims(token);
        if (claims.containsKey(claim))
            return Optional.of(claims.get(claim).as(clazz));
        return Optional.empty();
    }

    public <T> Optional<List<T>> getTokenClaimAsList(String token, String claim, Class<T> clazz) {
        Map<String, Claim> claims = getTokenClaims(token);
        if (claims.containsKey(claim))
            return Optional.of(claims.get(claim).asList(clazz));
        return Optional.empty();
    }

    /**
     * <h3>Verifies json that given in request</h3>
     * <p>
     * Verifies token signature end expiration date
     * </p>
     * 
     * @param jwtToken The token that should be verified
     * @return The status that indicates result of verification
     */
    public TokenVerificationStatus verifyJwtToken(String jwtToken, String[] audiences, String[] scopes) {
        try {
            Verification verification = JWT.require(ALGORITHM)
                    .withIssuer(issuer);
            if (audiences != null && audiences.length > 0)
                verification.withAudience(audiences);
            if (scopes != null && scopes.length > 0)
                verification.withArrayClaim(Constants.JwtClaims.AccessTokenClaims.SCOPE, scopes);
            JWTVerifier verifier = verification.build();
            verifier.verify(jwtToken);
            return TokenVerificationStatus.SUCCESS;
        } catch (AlgorithmMismatchException exception) {
            return TokenVerificationStatus.INVALID_ALGORITHM.withException(exception);
        } catch (SignatureVerificationException exception) {
            return TokenVerificationStatus.INVALID_SIGNATURE.withException(exception);
        } catch (TokenExpiredException exception) {
            return TokenVerificationStatus.EXPIRED.withException(exception);
        } catch (JWTVerificationException exception) {
            return TokenVerificationStatus.FAILED.withException(exception);
        }
    }

    public TokenVerificationStatus verifyJwtToken(String jwtToken) {
        return verifyJwtToken(jwtToken, null, null);
    }

}
