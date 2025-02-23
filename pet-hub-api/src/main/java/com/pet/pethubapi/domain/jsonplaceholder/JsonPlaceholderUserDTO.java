package com.pet.pethubapi.domain.jsonplaceholder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode
@ToString
public final class JsonPlaceholderUserDTO {

    private Integer id;
    private String name;
    private String email;
    private String website;

}
