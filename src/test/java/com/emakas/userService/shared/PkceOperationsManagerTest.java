package com.emakas.userService.shared;

import com.emakas.userService.shared.enums.CodeChallengeMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PkceOperationsManagerTest {
    private static final String VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
    private static final String S256_CHALLENGE = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";

    @Test
    void acceptsMatchingPlainChallenge() {
        assertTrue(PkceOperationsManager.checkCodeChallenge(VERIFIER, VERIFIER, CodeChallengeMethod.PLAIN));
    }

    @Test
    void rejectsMismatchedPlainChallenge() {
        assertFalse(PkceOperationsManager.checkCodeChallenge(VERIFIER, VERIFIER + "x", CodeChallengeMethod.PLAIN));
    }

    @Test
    void acceptsRfc7636S256Example() {
        assertTrue(PkceOperationsManager.checkCodeChallenge(S256_CHALLENGE, VERIFIER, CodeChallengeMethod.SHA_256));
    }

    @Test
    void rejectsS256ChallengeForDifferentVerifier() {
        assertFalse(PkceOperationsManager.checkCodeChallenge(
                S256_CHALLENGE, VERIFIER + "x", CodeChallengeMethod.SHA_256));
    }

    @ParameterizedTest
    @CsvSource({"plain, PLAIN", "S256, SHA_256", "unsupported, UNKNOWN"})
    void resolvesChallengeMethod(String value, CodeChallengeMethod expected) {
        assertEquals(expected, PkceOperationsManager.getCodeChallengeMethodFromString(value));
    }
}
