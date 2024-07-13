package com.pet.pethubapi.infrastructure.security;

import com.pet.pethubapi.domain.ApplicationProperties;
import com.pet.pethubapi.domain.role.Role;
import com.pet.pethubapi.infrastructure.security.impl.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContextConfiguration(classes = {ApplicationProperties.class, JwtServiceImpl.class})
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource("classpath:application-default.properties")
@ExtendWith(SpringExtension.class)
class JwtServiceShould {

    private final Date currentDate = new Date();

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private JwtService jwtService;

    @Test
    void generateTokens() {
        var mockRole1 = new Role(1, "admin");
        var mockRole2 = new Role(1, "user");

        var response = jwtService.generateTokens("username", Set.of(mockRole1, mockRole2));

        assertThat(response.accessToken()).isNotEmpty();
        assertThat(response.refreshToken()).isNotEmpty();

        var parsedAccessToken = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(response.accessToken());
        assertThat(parsedAccessToken.getHeader().isPayloadEncoded()).isTrue();
        assertThat(parsedAccessToken.getHeader().getAlgorithm()).isEqualTo("HS256");
        assertThat(parsedAccessToken.getPayload().getSubject()).isEqualTo("username");
        assertThat(parsedAccessToken.getPayload().getIssuer()).isEqualTo(JwtServiceImpl.ISSUER);
        assertThat(parsedAccessToken.getPayload().getIssuedAt()).isBefore(currentDate);
        assertThat(parsedAccessToken.getPayload().getExpiration()).isAfter(currentDate);
        assertThat(parsedAccessToken.getPayload().get(JwtServiceImpl.ACCESS_CLAIM, Boolean.class)).isTrue();
        assertThat(parsedAccessToken.getPayload().get(JwtServiceImpl.REFRESH_CLAIM, Boolean.class)).isNull();
        assertThat(parsedAccessToken.getPayload().get(JwtServiceImpl.AUTHORITIES_CLAIM, String.class)).contains("admin", "user");
        var parsedRefreshToken = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(response.refreshToken());
        assertThat(parsedRefreshToken.getHeader().isPayloadEncoded()).isTrue();
        assertThat(parsedRefreshToken.getHeader().getAlgorithm()).isEqualTo("HS256");
        assertThat(parsedRefreshToken.getPayload().getSubject()).isEqualTo("username");
        assertThat(parsedRefreshToken.getPayload().getIssuer()).isEqualTo(JwtServiceImpl.ISSUER);
        assertThat(parsedRefreshToken.getPayload().getIssuedAt()).isBefore(currentDate);
        assertThat(parsedRefreshToken.getPayload().getExpiration()).isAfter(currentDate);
        assertThat(parsedRefreshToken.getPayload().get(JwtServiceImpl.ACCESS_CLAIM, Boolean.class)).isNull();
        assertThat(parsedRefreshToken.getPayload().get(JwtServiceImpl.REFRESH_CLAIM, Boolean.class)).isTrue();
        assertThat(parsedRefreshToken.getPayload().get(JwtServiceImpl.AUTHORITIES_CLAIM, String.class)).isNull();
    }

    @Test
    void returnTrue_IfTokenHasExpired() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(currentDate)
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.hasTokenExpired(token)).isTrue();
    }

    @Test
    void returnFalse_IfTokenHasNotExpired() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.hasTokenExpired(token)).isFalse();
    }

    @Test
    void throwException_whenCheckingTokenExpiration_withUnsignedToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .compact();

        assertThatThrownBy(() -> jwtService.hasTokenExpired(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenCheckingTokenExpiration_withEmptyToken() {
        assertThatThrownBy(() -> jwtService.hasTokenExpired("  "))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void returnTrue_IfTokenIsAccessToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .claim(JwtServiceImpl.ACCESS_CLAIM, "true")
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.isAccessToken(token)).isTrue();
    }

    @Test
    void returnFalse_IfTokenIsNotAccessToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.isAccessToken(token)).isFalse();
    }

    @Test
    void throwException_whenCheckingIsAccessToken_withExpiredToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(currentDate)
            .signWith(getSignKey())
            .compact();

        assertThatThrownBy(() -> jwtService.isAccessToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenCheckingIsAccessToken_withUnsignedToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .compact();

        assertThatThrownBy(() -> jwtService.isAccessToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenCheckingIsAccessToken_withEmptyToken() {
        assertThatThrownBy(() -> jwtService.isAccessToken(null))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void returnTrue_IfTokenIsRefreshToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .claim(JwtServiceImpl.REFRESH_CLAIM, "true")
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }

    @Test
    void returnFalse_IfTokenIsNotRefreshToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.isRefreshToken(token)).isFalse();
    }

    @Test
    void throwException_whenCheckingIsRefreshToken_withExpiredToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(currentDate)
            .signWith(getSignKey())
            .compact();

        assertThatThrownBy(() -> jwtService.isRefreshToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenCheckingIsRefreshToken_withUnsignedToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .compact();

        assertThatThrownBy(() -> jwtService.isRefreshToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenCheckingIsRefreshToken_withEmptyToken() {
        assertThatThrownBy(() -> jwtService.isRefreshToken(""))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void getUsernameFromToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("username");
    }

    @Test
    void returnNull_whenGetUsernameFromToken_ifTokenHasExpired() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(currentDate)
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.getUsernameFromToken(token)).isNull();
    }

    @Test
    void throwException_whenGetUsernameFromToken_withExpiredToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(currentDate)
            .signWith(getSignKey())
            .compact();

        assertThat(jwtService.getUsernameFromToken(token)).isNull();
    }

    @Test
    void throwException_whenGetUsernameFromToken_withUnsignedToken() {
        var token = Jwts.builder()
            .subject("username")
            .issuedAt(currentDate)
            .expiration(new Date(currentDate.getTime() + 10000))
            .compact();

        assertThatThrownBy(() -> jwtService.getUsernameFromToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void throwException_whenGetUsernameFromToken_withEmptyToken() {
        assertThatThrownBy(() -> jwtService.getUsernameFromToken(""))
            .isInstanceOf(InvalidTokenException.class);
    }

    private SecretKey getSignKey() {
        final var encodedKey = applicationProperties.getJwt().getSecretKey();
        final var decodedKey = Base64.getDecoder().decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}
