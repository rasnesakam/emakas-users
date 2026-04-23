package com.emakas.userService.shared;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Base64;
import java.util.regex.Pattern;

public class StringUtils {
    private static final int SECRET_LENGTH = 256;
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String getRandomString(int length) {
        String alphabet = "abcdefghijklmnoprsqtuvwxyz";
        String numbers = "0123456789";
        String upperCase = alphabet.toUpperCase();
        String specialChars = "!^+%&()=?*";
        String stringPool = alphabet + numbers + upperCase + specialChars;
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++){
            password.append(stringPool.charAt(secureRandom.nextInt(length)));
        }
        return password.toString();
    }

    public static String generateSecretKey() {
        byte[] randomBytes = new byte[SECRET_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(randomBytes);
    }

    public static String toAscii(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String stripped = pattern.matcher(normalized).replaceAll("");
        return stripped.replaceAll("[^\\p{ASCII}]", ""); // kalan non-ASCII'leri de sil
    }

    public static String normalizeString(String str) {
        String asciiConverted = toAscii(str.toUpperCase());
        return asciiConverted.toLowerCase().replaceAll("\\s+", "_");
    }
}
