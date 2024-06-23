package com.pet.pethubapi.application.auth;

import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;

public interface AuthenticationService {

    void registerNewUser(RegisterDTO input);

    /**
     * Authenticates against existing users and returns access and refresh JWT.
     */
    JWTResponse authenticateUser(LoginDTO input);

    /**
     * Accepts refresh token to re-authenticate user.
     */
    JWTResponse refreshToken(String refreshToken);

}
