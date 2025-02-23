package com.pet.pethubapi.domain.jsonplaceholder;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public final class JsonPlaceholderTodoDTO {

    private Integer id;
    private String title;
    @JsonAlias("completed")
    private Boolean isCompleted;
    private Integer userId;

}
