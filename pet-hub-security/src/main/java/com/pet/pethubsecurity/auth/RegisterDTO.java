package com.pet.pethubsecurity.auth;

import lombok.Data;

@Data
public final class RegisterDTO {

    private String email;
    private String password; // don't change property name, coupled with logging
    private String firstName;
    private String lastName;

}
