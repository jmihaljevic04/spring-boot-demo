package com.pet.pethubapi.application.auth;

import com.pet.pethubsecurity.JWTResponse;
import com.pet.pethubsecurity.domain.role.Role;
import com.pet.pethubsecurity.domain.role.RoleEnum;
import com.pet.pethubsecurity.domain.user.User;
import com.pet.pethubsecurity.domain.user.UserRepository;
import com.pet.pethubsecurity.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
class AuthenticationServiceImpl implements AuthenticationService {

    private static final Pattern EMAIL_VALIDATOR = Pattern.compile("^[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,4}$");
    // at least one small and one capital letter, at least one digit, at least one special symbol, and length [8,20]
    private static final Pattern PASSWORD_VALIDATOR = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).(?=.*[@#$%&+=-\\\\!.]).{8,20}$");

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerNewUser(RegisterDTO input) {
        validateRegisterInput(input);

        final var userRoles = new HashSet<Role>(1);
        final var userRole = new Role();
        userRole.setId(RoleEnum.USER.getId());
        userRoles.add(userRole);

        final var user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setRoles(userRoles);
        user.setCreatedBy("NEW_USER");

        userRepository.save(user);
    }

    @Override
    public JWTResponse authenticateUser(LoginDTO input) {
        validateLoginInput(input);

        final var user = userRepository.findByEmail(input.email());
        if (user.isEmpty()) {
            throw new UnauthorizedException("Invalid email or password!");
        }

        try {
            final var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(input.email(), input.password(), user.get().getAuthorities()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password!");
        }

        return jwtService.generateTokens(user.get().getEmail(), user.get().getRoles());
    }

    @Override
    public JWTResponse refreshAuthToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidAuthenticationException("Refresh token is empty!");
        }

        if (jwtService.hasTokenExpired(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token has expired or is invalid!");
        }

        final var user = userRepository.findByEmail(jwtService.getUsernameFromToken(refreshToken)).get();
        return jwtService.generateTokens(user.getEmail(), user.getRoles());
    }

    private void validateRegisterInput(RegisterDTO input) {
        if (input == null) {
            throw new InvalidAuthenticationException("Input for new user registration is empty!");
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

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new InvalidAuthenticationException("User with given email already exists!");
        }

        if (!PASSWORD_VALIDATOR.matcher(input.getPassword()).matches()) {
            throw new InvalidAuthenticationException(
                "Invalid password format! Password must contain at least 8 characters and at most 20 characters, with at least one uppercase and one lowercase letter and at least one digit and special character!");
        }
    }

    private static void validateLoginInput(LoginDTO input) {
        if (input == null) {
            throw new InvalidAuthenticationException("Input for user login is empty!");
        }

        if (StringUtils.isAnyBlank(input.email(), input.password())) {
            throw new InvalidAuthenticationException("One of the inputs for user login is empty!");
        }
    }

}
