package com.pet.pethubapi.domain.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class RegisterDTO {

    private String email;
    private String password; // don't change property name, coupled with logging
    private String firstName;
    private String lastName;

}
