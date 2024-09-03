package com.pet.pethubapi.interfaces.auth;

import com.pet.pethubapi.application.auth.AuthenticationService;
import com.pet.pethubapi.application.auth.LoginDTO;
import com.pet.pethubapi.application.auth.RegisterDTO;
import com.pet.pethubsecurity.JWTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody RegisterDTO request) {
        authenticationService.registerNewUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JWTResponse> authenticate(@RequestBody LoginDTO request) {
        final var response = authenticationService.authenticateUser(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/refresh-token", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JWTResponse> refreshToken(@RequestHeader("X-Auth-Refresh") String refreshToken) {
        final var response = authenticationService.refreshAuthToken(refreshToken);
        return ResponseEntity.ok().body(response);
    }

}
