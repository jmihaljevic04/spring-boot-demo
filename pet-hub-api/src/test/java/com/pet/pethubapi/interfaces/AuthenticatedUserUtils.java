package com.pet.pethubapi.interfaces;

import com.pet.pethubsecurity.domain.role.Role;
import com.pet.pethubsecurity.domain.role.RoleEnum;
import com.pet.pethubsecurity.jwt.JwtService;
import io.restassured.http.Header;

import java.util.Set;

/**
 * Generates real JWT with real users stored in database. Works only for integration tests.
 * Implemented because @WithMockUser and similar annotations don't work with JWT and custom JWT HTTP request filter.
 */
public final class AuthenticatedUserUtils {

    private AuthenticatedUserUtils() {

    }

    public static Header generateAdminJwt(JwtService jwtService) {
        var adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN.getName());
        var jwt = jwtService.generateTokens("admin@pethub.com", Set.of(adminRole)).accessToken();
        return new Header("Authorization", "Bearer " + jwt);
    }

    public static Header generateUserJwt(JwtService jwtService) {
        var adminRole = new Role();
        adminRole.setName(RoleEnum.USER.getName());
        var jwt = jwtService.generateTokens("user@pethub.com", Set.of(adminRole)).accessToken();
        return new Header("Authorization", "Bearer " + jwt);
    }

}
