package com.emakas.userService.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.User;
import com.emakas.userService.model.Token;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.TokenType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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

    private final String issuer;

    private final Algorithm ALGORITHM;
    private final String appDomainName;
    private final UserService userService;


    public TokenManager(
            @Value("${java-jwt.issuer}") String issuer,
            @Value("${java-jwt.secret}") String jwtSecret,
            @Value("${java-jwt.expiration}") long secondsToExpire,
            @Value("${app.domain}") String appDomainName,
            UserService userService) {
        this.issuer = issuer;
        this.secondsToExpire = secondsToExpire;

        ALGORITHM = Algorithm.HMAC256(jwtSecret);
        this.appDomainName = appDomainName;
        this.userService = userService;
    }

    private Token createToken(UUID subjectId, TokenType tokenType, long expireDateSecond, @Nullable String[] audience, @Nullable String[] scopes) {
        Token token = new Token();
        token.setIss(issuer);
        if (audience != null && audience.length > 0)
            token.setAud(Set.of(audience));
        if (scopes != null && scopes.length > 0)
            token.setScope(Set.of(scopes));
        token.setSub(tokenType.toString().concat(Constants.SEPARATOR).concat(subjectId.toString()));
        token.setIat(Instant.now().getEpochSecond());
        token.setExp(expireDateSecond);
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }

    public Optional<User> loadUserFromToken(Token token){
        String tokenSubject = token.getSub();
        Pattern tokenSubjectPattern = Pattern.compile(String.format("^%s:(.*)$",TokenType.USR));
        Matcher tokenSubjectMatcher = tokenSubjectPattern.matcher(tokenSubject);
        if (!tokenSubjectMatcher.find())
            return Optional.empty();
        UUID userId = UUID.fromString(tokenSubjectMatcher.group(1));
        return userService.getById(userId);
    }

    public Token createUserAccessToken(User user, @Nullable String[] audiences, @Nullable String[] scopes){

        return createToken(user.getId(), TokenType.USR, Instant.now().plus(secondsToExpire, ChronoUnit.SECONDS).getEpochSecond(), audiences, scopes);
    }

    //TODO: Find a suitable place for REFRESH_TOKEN string
    public Token createUserRefreshToken(User user, String... audiences){
        String refreshScope = REFRESH_TOKEN.concat(SEPARATOR).concat(AccessModifier.WRITE.toString());
        return createToken(
                user.getId(), TokenType.USR,
                Instant.now().plus(25, ChronoUnit.MINUTES).getEpochSecond(),
                audiences,
                new String[]{refreshScope}
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
                .sign(ALGORITHM);
    }

    public Optional<Token> getFromToken(@NonNull String token){
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String sub = decodedJWT.getSubject();
            TokenType tokenType = TokenType.fromString(sub);
            if (tokenType == TokenType.UNDEFINED)
                throw new JWTDecodeException("Undefined token type");
            sub = getCleanSubject(tokenType, sub).orElseThrow(() -> new RuntimeException("Unknown token."));
            return Optional.of(new Token(
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

    public Optional<String> getCleanSubject(@NonNull TokenType tokenType, String tokenSubject) {
        Pattern tokenPattern = Pattern.compile(String.format("^%s%s(.*)$",tokenType, SEPARATOR));
        Matcher matcher = tokenPattern.matcher(tokenSubject);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    public Token generateUserToken(User user, String... audiences) {
        Token token = new Token();
        token.setIss(issuer);
        token.setJti(UUID.randomUUID().toString());
        token.setSub(user.getId().toString());
        if (audiences != null)
            token.setAud(Set.of(audiences));
        token.setExp(Instant.now().plusSeconds(secondsToExpire).getEpochSecond());
        token.setSerializedToken(generateJwtToken(token));
        return token;
    }
}
