package com.emakas.userService.shared;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;

public class RequestUtils {
    public static String getRequestFingerPrint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");
        String canvasHash = request.getHeader("X-Canvas-Fingerprint");
        String screenRes = request.getHeader("X-Screen-Resolution");
        String rawData = String.join("|", userAgent, acceptLanguage, canvasHash, screenRes);
        return HashUtils.sha256Hex(rawData);
    }
}
