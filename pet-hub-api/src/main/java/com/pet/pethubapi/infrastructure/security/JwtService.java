package com.pet.pethubapi.infrastructure.security;

import com.pet.pethubapi.application.auth.JWTResponse;

public interface JwtService {

    JWTResponse generateTokens(String username);

    boolean hasTokenExpired(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String getUsernameFromToken(String token);

}
