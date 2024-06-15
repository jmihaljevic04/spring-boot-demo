package com.pet.pethubapi.application.auth.impl;

import com.pet.pethubapi.application.auth.AuthenticationService;
import com.pet.pethubapi.application.auth.JwtService;
import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;
import com.pet.pethubapi.domain.user.User;
import com.pet.pethubapi.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerNewUser(RegisterDTO registerDTO) {
        final var user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setCreatedBy("NEW_USER");

        userRepository.save(user);
    }

    @Override
    public String authenticateUser(LoginDTO loginDTO) {
        final var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtService.generateToken(loginDTO.email());
    }

}
