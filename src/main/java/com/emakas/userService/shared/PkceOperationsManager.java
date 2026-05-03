package com.emakas.userService.shared;

import com.emakas.userService.shared.enums.CodeChallengeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PkceOperationsManager {

    private static final Logger logger = LoggerFactory.getLogger(PkceOperationsManager.class);

    private static boolean checkCodeChallengePlain(String codeChallenge, String codeVerifier) {
        return codeChallenge.equals(codeVerifier);
    }

    private static boolean checkCodeChallengeS256(String codeChallenge, String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(codeVerifier.getBytes());
            String encoded = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hashed);
            return encoded.equals(codeChallenge);
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    public static boolean checkCodeChallenge(String codeChallenge, String codeVerifier, CodeChallengeMethod codeChallengeMethod) {
        return switch (codeChallengeMethod) {
            case PLAIN -> checkCodeChallengePlain(codeChallenge, codeVerifier);
            case SHA_256 -> checkCodeChallengeS256(codeChallenge, codeVerifier);
            default -> false;
        };
    }

    public static CodeChallengeMethod getCodeChallengeMethodFromString(String value) {
        if (value.equals(CodeChallengeMethod.SHA_256.getNormalizedName()))
            return CodeChallengeMethod.SHA_256;
        else if (value.equals(CodeChallengeMethod.PLAIN.getNormalizedName()))
            return CodeChallengeMethod.PLAIN;
        else return CodeChallengeMethod.UNKNOWN;
    }
}
