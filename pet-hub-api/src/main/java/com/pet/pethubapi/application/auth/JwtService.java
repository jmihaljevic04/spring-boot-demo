package com.pet.pethubapi.application.auth;

public interface JwtService {

    String generateToken(String username);

    boolean isTokenExpired(String token);

    String getUsernameFromToken(String token);

}
