package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenManagerTest {
    private static final String ISSUER = "https://issuer.example";
    private static final String AUDIENCE = "users-api";
    private static final String SCOPE = "users:read";

    private static TokenManager tokenManager;
    private static Algorithm signingAlgorithm;
    private static Algorithm otherSigningAlgorithm;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keys = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
        signingAlgorithm = Algorithm.RSA256(publicKey, privateKey);

        KeyPair otherKeys = generator.generateKeyPair();
        otherSigningAlgorithm = Algorithm.RSA256(
                (RSAPublicKey) otherKeys.getPublic(), (RSAPrivateKey) otherKeys.getPrivate());

        RsaKeyFactory keyFactory = mock(RsaKeyFactory.class);
        when(keyFactory.getPublicKey()).thenReturn(publicKey);
        when(keyFactory.getPrivateKey()).thenReturn(privateKey);
        tokenManager = new TokenManager(ISSUER, "unused-test-secret", 300, 7, 300, ISSUER, keyFactory, null);
    }

    @Test
    void acceptsValidTokenWithExpectedAudienceAndScope() {
        String token = tokenBuilder().sign(signingAlgorithm);

        assertEquals(TokenVerificationStatus.SUCCESS,
                tokenManager.verifyJwtToken(token, new String[]{AUDIENCE}, new String[]{SCOPE}));
    }

    @Test
    void rejectsExpiredToken() {
        String token = tokenBuilder().withExpiresAt(Instant.EPOCH).sign(signingAlgorithm);

        assertEquals(TokenVerificationStatus.EXPIRED, tokenManager.verifyJwtToken(token));
    }

    @Test
    void rejectsUnsignedToken() {
        String token = tokenBuilder().sign(Algorithm.none());

        assertEquals(TokenVerificationStatus.INVALID_ALGORITHM, tokenManager.verifyJwtToken(token));
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String token = tokenBuilder().sign(otherSigningAlgorithm);

        assertEquals(TokenVerificationStatus.INVALID_SIGNATURE, tokenManager.verifyJwtToken(token));
    }

    @Test
    void rejectsUnexpectedIssuer() {
        String token = tokenBuilder().withIssuer("https://other.example").sign(signingAlgorithm);

        assertEquals(TokenVerificationStatus.FAILED, tokenManager.verifyJwtToken(token));
    }

    @Test
    void rejectsMissingRequiredAudience() {
        String token = tokenBuilder().sign(signingAlgorithm);

        assertEquals(TokenVerificationStatus.FAILED,
                tokenManager.verifyJwtToken(token, new String[]{"another-api"}, null));
    }

    @Test
    void rejectsMissingRequiredScope() {
        String token = tokenBuilder().sign(signingAlgorithm);

        assertEquals(TokenVerificationStatus.FAILED,
                tokenManager.verifyJwtToken(token, null, new String[]{SCOPE, "users:write"}));
    }

    private static JWTCreator.Builder tokenBuilder() {
        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withArrayClaim(Constants.JwtClaims.AccessTokenClaims.SCOPE, new String[]{SCOPE})
                .withExpiresAt(Instant.now().plusSeconds(300));
    }
}
