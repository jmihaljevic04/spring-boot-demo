package com.pet.pethubapi.application.auth;

import com.pet.pethubapi.domain.role.Role;

import java.util.Set;

public interface JwtService {

    JWTResponse generateTokens(String username, Set<Role> authorities);

    boolean hasTokenExpired(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String getUsernameFromToken(String token);

}
