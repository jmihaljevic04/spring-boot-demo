package com.pet.pethubapi.domain.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {

    ADMIN(1, "admin"), USER(2, "user");

    private final Integer id;
    private final String name;

}
