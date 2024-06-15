package com.pet.pethubapi.interfaces.auth;

import com.pet.pethubapi.application.auth.AuthenticationService;
import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> authenticate(@RequestBody LoginDTO request) {
        final var token = authenticationService.authenticateUser(request);
        return ResponseEntity.ok().body(token);
    }

}
