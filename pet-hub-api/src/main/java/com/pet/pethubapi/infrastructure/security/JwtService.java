package com.pet.pethubapi.infrastructure.security;

import com.pet.pethubapi.application.auth.JWTResponse;
import com.pet.pethubapi.domain.role.Role;

import java.util.Set;

public interface JwtService {

    JWTResponse generateTokens(String username, Set<Role> authorities);

    boolean hasTokenExpired(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String getUsernameFromToken(String token);

}
