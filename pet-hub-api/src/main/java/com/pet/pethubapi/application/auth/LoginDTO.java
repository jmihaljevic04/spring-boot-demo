package com.pet.pethubapi.application.auth;

public record LoginDTO(String email, String password) {
    // don't change password property name, coupled with logging
}
