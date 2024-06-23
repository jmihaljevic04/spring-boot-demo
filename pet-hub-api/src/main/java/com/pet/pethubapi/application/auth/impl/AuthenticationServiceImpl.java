package com.pet.pethubapi.application.auth.impl;

import com.pet.pethubapi.application.auth.AuthenticationService;
import com.pet.pethubapi.application.auth.InvalidAuthenticationException;
import com.pet.pethubapi.application.auth.JWTResponse;
import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;
import com.pet.pethubapi.domain.user.User;
import com.pet.pethubapi.domain.user.UserRepository;
import com.pet.pethubapi.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    // strict email regex validator
    private static final Pattern EMAIL_VALIDATOR = Pattern.compile(
        "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$");
    // at least one small and one capital letter, at least one digit, at least one special symbol, and length [8,20]
    private static final Pattern PASSWORD_VALIDATOR = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,}$\n");

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerNewUser(RegisterDTO input) {
        validateRegisterInput(input);

        final var user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setCreatedBy("NEW_USER");

        userRepository.save(user);
    }

    @Override
    public JWTResponse authenticateUser(LoginDTO input) {
        validateLoginInput(input);

        final var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.email(), input.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtService.generateTokens(input.email());
    }

    @Override
    public JWTResponse refreshToken(String refreshToken) {
        if (jwtService.hasTokenExpired(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidAuthenticationException("Refresh token has expired or is invalid!");
        }

        final var username = jwtService.getUsernameFromToken(refreshToken);

        return jwtService.generateTokens(username);
    }

    private static void validateRegisterInput(RegisterDTO input) {
        if (input == null) {
            throw new InvalidAuthenticationException("Input for new user registration is null!");
        }

        if (StringUtils.isAnyBlank(input.getEmail(), input.getPassword(), input.getFirstName(), input.getLastName())) {
            throw new InvalidAuthenticationException("One of the inputs for new user registration is empty!");
        }

        if (StringUtils.length(input.getFirstName()) > 255 || StringUtils.length(input.getLastName()) > 255) {
            throw new InvalidAuthenticationException("First or last name should be shorter than 256 characters!");
        }

        if (!EMAIL_VALIDATOR.matcher(input.getEmail()).matches()) {
            throw new InvalidAuthenticationException("Invalid email address format!");
        }

        if (!PASSWORD_VALIDATOR.matcher(input.getPassword()).matches()) {
            throw new InvalidAuthenticationException(
                "Invalid password format! Password must contain at least 8 characters and at most 20 characters, with at least one uppercase and one lowercase letter and at least one digit and special character!");
        }
    }

    private static void validateLoginInput(LoginDTO input) {
        if (input == null) {
            throw new InvalidAuthenticationException("Input for user login is null!");
        }

        if (StringUtils.isAnyBlank(input.email(), input.password())) {
            throw new InvalidAuthenticationException("One of the inputs for user login is empty!");
        }
    }

}
