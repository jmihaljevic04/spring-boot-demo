package com.pet.pethubapi.infrastructure.security.impl;

import com.pet.pethubapi.domain.ApplicationProperties;
import com.pet.pethubapi.domain.role.Role;
import com.pet.pethubapi.infrastructure.security.InvalidTokenException;
import com.pet.pethubapi.infrastructure.security.JWTResponse;
import com.pet.pethubapi.infrastructure.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private static final String ADMIN_USERNAME = "admin";
    public static final String ISSUER = "PetHub";
    public static final String ACCESS_CLAIM = "access_token";
    public static final String REFRESH_CLAIM = "refresh_token";
    public static final String AUTHORITIES_CLAIM = "authorities";

    private final ApplicationProperties applicationProperties;

    @Override
    public JWTResponse generateTokens(String username, Set<Role> authorities) {
        final var currentDate = new Date();
        final var accessToken = generateAccessToken(username, currentDate, authorities);
        final var refreshToken = generateRefreshToken(username, currentDate);

        return new JWTResponse(accessToken, refreshToken);
    }

    @Override
    public boolean hasTokenExpired(String token) {
        try {
            final var expiration = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        }
    }

    @Override
    public boolean isAccessToken(String token) {
        try {
            final var claim = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().get(ACCESS_CLAIM);
            if (claim == null) {
                return false;
            }

            return Boolean.parseBoolean(String.valueOf(claim));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        }
    }

    @Override
    public boolean isRefreshToken(String token) {
        try {
            final var claim = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().get(REFRESH_CLAIM);
            if (claim == null) {
                return false;
            }

            return Boolean.parseBoolean(String.valueOf(claim));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        if (hasTokenExpired(token)) {
            return null;
        }

        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private String generateAccessToken(String username, Date currentDate, Set<Role> authorities) {
        final var stringifiedRoles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList().toString();

        return Jwts.builder()
            .subject(username)
            .issuedAt(currentDate)
            .issuer(ISSUER)
            .expiration(getExpirationInterval(username, currentDate))
            .claim(ACCESS_CLAIM, Boolean.TRUE)
            .claim(AUTHORITIES_CLAIM, stringifiedRoles)
            .signWith(getSignKey())
            .compact();
    }

    private String generateRefreshToken(String username, Date currentDate) {
        final var accessTokenExpiration = getExpirationInterval(username, currentDate);
        final var expiration = applicationProperties.getJwt().getRefreshExpiration();

        return Jwts.builder()
            .subject(username)
            .issuedAt(currentDate)
            .issuer(ISSUER)
            .expiration(new Date(accessTokenExpiration.getTime() + expiration))
            .claim(REFRESH_CLAIM, Boolean.TRUE)
            .signWith(getSignKey())
            .compact();
    }

    private Date getExpirationInterval(String username, Date currentDate) {
        final var isAdmin = ADMIN_USERNAME.equals(username);

        final int expirationInterval;
        if (isAdmin) {
            expirationInterval = applicationProperties.getJwt().getAdminAccessExpiration();
        } else {
            expirationInterval = applicationProperties.getJwt().getAccessExpiration();
        }

        return new Date(currentDate.getTime() + expirationInterval);
    }

    private SecretKey getSignKey() {
        final var encodedKey = applicationProperties.getJwt().getSecretKey();
        final var decodedKey = Base64.getDecoder().decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}
