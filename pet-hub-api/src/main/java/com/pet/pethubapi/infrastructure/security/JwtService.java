package com.pet.pethubapi.infrastructure.security;

public interface JwtService {

    String generateToken(String username);

    boolean isTokenExpired(String token);

    String getUsernameFromToken(String token);

}
