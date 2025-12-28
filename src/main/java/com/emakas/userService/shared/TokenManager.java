package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.User;
import com.emakas.userService.model.Token;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.TokenTargetType;
import com.emakas.userService.shared.enums.TokenType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.emakas.userService.shared.Constants.REFRESH_TOKEN;
import static com.emakas.userService.shared.Constants.SEPARATOR;

@Component
public class TokenManager implements Serializable {

    private final long secondsToExpire;
    private final int daysToExpire;
    private final String issuer;
    private final Algorithm ALGORITHM;
    private final String appDomainName;
    private final UserService userService;


    public TokenManager(
            @Value("${java-jwt.issuer}") String issuer,
            @Value("${java-jwt.secret}") String jwtSecret,
            @Value("${java-jwt.expiration}") long secondsToExpire,
            @Value("${java-jwt.refresh_expiration}") int daysToExpire,
            @Value("${app.domain}") String appDomainName,
            UserService userService) {
        this.issuer = issuer;
        this.secondsToExpire = secondsToExpire;
        this.daysToExpire = daysToExpire;
        ALGORITHM = Algorithm.HMAC256(jwtSecret);
        this.appDomainName = appDomainName;
        this.userService = userService;
    }

    private Token createToken(UUID subjectId, TokenTargetType tokenTargetType, long expireDateSecond, @Nullable String[] audience, @Nullable String[] scopes, TokenType tokenType, UUID clientId) {
        Token token = new Token();
        token.setIss(issuer);
        if (audience != null && audience.length > 0)
            token.setAud(Set.of(audience));
        if (scopes != null && scopes.length > 0)
            token.setScope(Set.of(scopes));
        token.setSub(tokenTargetType.toString().concat(Constants.SEPARATOR).concat(subjectId.toString()));
        token.setIat(Instant.now().getEpochSecond());
        token.setJti(UUID.randomUUID());
        token.setExp(expireDateSecond);
        token.setTokenType(tokenType);
        token.setClientId(clientId);
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }

    public Optional<User> loadUserFromToken(Token token){
        UUID userId = null;
        String tokenSubject = token.getSub();
        Pattern tokenSubjectPattern = Pattern.compile(String.format("^%s:(.*)$", TokenTargetType.USR));
        Matcher tokenSubjectMatcher = tokenSubjectPattern.matcher(tokenSubject);
        if (tokenSubjectMatcher.find())
            userId = UUID.fromString(tokenSubjectMatcher.group(1));
        else{
            try {
                userId = UUID.fromString(token.getSub());
            }
            catch (Exception e) {
                return Optional.empty();
            }
        }
        return userService.getById(userId);
    }

    public Token createUserAccessToken(User user, @Nullable String[] audiences, @Nullable String[] scopes, UUID clientId){

        return createToken(user.getId(), TokenTargetType.USR, Instant.now().plus(secondsToExpire, ChronoUnit.SECONDS).getEpochSecond(), audiences, scopes, TokenType.ACCESS_TOKEN, clientId);
    }

    //TODO: Find a suitable place for REFRESH_TOKEN string
    public Token createUserRefreshToken(User user, UUID clientId, String[] audiences){
        String refreshScope = REFRESH_TOKEN.concat(SEPARATOR).concat(AccessModifier.WRITE.toString());
        return createToken(
                user.getId(), TokenTargetType.USR,
                Instant.now().plus(daysToExpire, ChronoUnit.DAYS).getEpochSecond(),
                audiences,
                new String[]{refreshScope},
                TokenType.REFRESH_TOKEN,
                clientId
        );
    }

    public Token createApplicationAccessToken(Application application, @Nullable String[] audiences, @Nullable String[] scopes, UUID clientId){
        return createToken(application.getId(), TokenTargetType.APP, Instant.now().plus(secondsToExpire, ChronoUnit.SECONDS).getEpochSecond(), audiences, scopes, TokenType.ACCESS_TOKEN, clientId);
    }

    public Token createApplicationRefreshToken(Application application, UUID clientId, String[] audiences){
        String refreshScope = REFRESH_TOKEN.concat(SEPARATOR).concat(AccessModifier.WRITE.toString());
        return createToken(
                application.getId(), TokenTargetType.APP,
                Instant.now().plus(daysToExpire, ChronoUnit.DAYS).getEpochSecond(),
                audiences,
                new String[]{refreshScope},
                TokenType.REFRESH_TOKEN,
                clientId
        );
    }

    public String generateJwtToken(Token token) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(token.getSub())
                .withAudience(
                        Optional.ofNullable(token.getAud())
                                .orElse(new HashSet<>())
                                .toArray(new String[0]
                ))
                .withClaim("scope", token.getScope().stream().toList())
                .withExpiresAt(Instant.ofEpochSecond(token.getExp()))
                .withIssuedAt(Instant.ofEpochSecond(token.getIat()))
                .withJWTId(token.getJti().toString())
                .sign(ALGORITHM);
    }

    public Optional<Token> getFromToken(@NotNull String token){
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String sub = decodedJWT.getSubject();
            TokenTargetType tokenTargetType = TokenTargetType.fromString(sub);
            if (tokenTargetType == TokenTargetType.UNDEFINED)
                throw new JWTDecodeException("Undefined token type");
            sub = getCleanSubject(tokenTargetType, sub).orElseThrow(() -> new RuntimeException("Unknown token."));
            Token generatedToken = new Token();
            generatedToken.setSub(sub);
            generatedToken.setJti(UUID.fromString(decodedJWT.getId()));
            generatedToken.setIat(decodedJWT.getIssuedAt().getTime());
            generatedToken.setExp(decodedJWT.getExpiresAt().getTime());
            generatedToken.setAud(new HashSet<>(decodedJWT.getAudience()));
            generatedToken.setScope(new HashSet<>(decodedJWT.getClaim("scope").asList(String.class)));
            generatedToken.setSerializedToken(decodedJWT.getToken());
            generatedToken.setTokenTargetType(tokenTargetType);
            return Optional.of(generatedToken);
        }
        catch (JWTDecodeException exception){
            return Optional.empty();
        }
    }
    public Map<String, Claim> getTokenClaims(@NotNull String token) {
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

    public Optional<String> getCleanSubject(@NotNull TokenTargetType tokenTargetType, String tokenSubject) {
        Pattern tokenPattern = Pattern.compile(String.format("^%s%s(.*)$", tokenTargetType, SEPARATOR));
        Matcher matcher = tokenPattern.matcher(tokenSubject);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    public Token generateUserToken(User user, String... audiences) {
        Token token = new Token();
        token.setIss(issuer);
        token.setSub(user.getId().toString());
        if (audiences != null)
            token.setAud(Set.of(audiences));
        token.setExp(Instant.now().plusSeconds(secondsToExpire).getEpochSecond());
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }
}
