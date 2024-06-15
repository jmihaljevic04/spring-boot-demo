package com.pet.pethubapi.application.auth.impl;

import com.pet.pethubapi.application.auth.JwtService;
import com.pet.pethubapi.infrastructure.ApplicationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final ApplicationProperties applicationProperties;

    @Override
    public String generateToken(String username) {
        final var expirationInterval = applicationProperties.getJwt().getExpiration();

        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationInterval))
            .issuer("PetHub")
            .signWith(getSignKey())
            .compact();
    }

    @Override
    public boolean isTokenExpired(String token) {
        final var expiration = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    @Override
    public String getUsernameFromToken(String token) {
        if (isTokenExpired(token)) {
            return null;
        }

        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey getSignKey() {
        final var encodedKey = applicationProperties.getJwt().getSecretKey();
        final var decodedKey = Base64.getDecoder().decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}
