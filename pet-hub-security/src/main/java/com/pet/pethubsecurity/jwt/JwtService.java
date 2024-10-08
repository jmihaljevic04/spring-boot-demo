package com.pet.pethubsecurity.jwt;

import com.pet.pethubsecurity.JWTResponse;
import com.pet.pethubsecurity.domain.role.Role;

import java.util.Set;

public interface JwtService {

    JWTResponse generateTokens(String username, Set<Role> authorities);

    boolean hasTokenExpired(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String getUsernameFromToken(String token);

}
