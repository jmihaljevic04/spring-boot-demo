package com.pet.pethubapi.application.auth;

import com.pet.pethubapi.domain.auth.LoginDTO;
import com.pet.pethubapi.domain.auth.RegisterDTO;

public interface AuthenticationService {

    void registerNewUser(RegisterDTO registerDTO);

    /**
     * Authenticates against existing users and returns JWT.
     *
     * @return generated token
     */
    String authenticateUser(LoginDTO loginDTO);

}
