package com.pet.pethubapi.infrastructure.jsonplaceholder;

import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderTodoDTO;
import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

public interface JsonPlaceholderClient {

    @GetExchange("/users")
    ResponseEntity<List<JsonPlaceholderUserDTO>> findAllUsers();

    @GetExchange("/users/{id}")
    ResponseEntity<JsonPlaceholderUserDTO> findUserById(@PathVariable Integer id);

    @PostExchange("/users")
    ResponseEntity<JsonPlaceholderUserDTO> createUser(@RequestBody JsonPlaceholderUserDTO user);

    @PutExchange("/users/{id}")
    ResponseEntity<JsonPlaceholderUserDTO> updateUser(@PathVariable Integer id, @RequestBody JsonPlaceholderUserDTO user);

    @PatchExchange("/users/{id}")
    ResponseEntity<JsonPlaceholderUserDTO> patchUser(@PathVariable Integer id, @RequestBody JsonPlaceholderUserDTO user);

    @DeleteExchange("/users/{id}")
    ResponseEntity<Void> deleteUserById(@PathVariable Integer id);

    @GetExchange("/todos")
    ResponseEntity<List<JsonPlaceholderTodoDTO>> findAllTodosByUserId(@RequestParam("userId") Integer userId);

}
