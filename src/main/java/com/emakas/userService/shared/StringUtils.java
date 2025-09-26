package com.emakas.userService.shared;

import org.jetbrains.annotations.NotNull;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
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

    public static String toAscii(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String stripped = pattern.matcher(normalized).replaceAll("");
        return stripped.replaceAll("[^\\p{ASCII}]", ""); // kalan non-ASCII'leri de sil
    }

    public static String normalizeString(@NotNull String str) {
        String asciiConverted = toAscii(str.toUpperCase());
        return asciiConverted.toLowerCase().replaceAll("\\s+", "_");
    }
}
