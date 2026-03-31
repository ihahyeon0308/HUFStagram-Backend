package com.hufs.haigram.backend.api;

import org.springframework.util.StringUtils;

public final class ApiSecuritySupport {

    private ApiSecuritySupport() {
    }

    public static String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 필요합니다.");
        }
        return authorizationHeader.substring(7).trim();
    }
}
