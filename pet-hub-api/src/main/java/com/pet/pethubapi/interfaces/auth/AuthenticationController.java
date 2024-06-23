package com.pet.pethubapi.interfaces.auth;

import com.pet.pethubapi.application.auth.AuthenticationService;
import com.pet.pethubapi.application.auth.JWTResponse;
import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDTO request) {
        authenticationService.registerNewUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> authenticate(@RequestBody LoginDTO request) {
        final var response = authenticationService.authenticateUser(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<JWTResponse> refreshToken(@RequestHeader("X-Auth-Refresh") String refreshToken) {
        final var response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok().body(response);
    }

}
