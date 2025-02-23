package com.pet.pethubapi.interfaces.jsonplaceholder;

import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderTodoDTO;
import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderUserDTO;
import com.pet.pethubapi.infrastructure.jsonplaceholder.JsonPlaceholderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mock")
class JsonPlaceholderController implements JsonPlaceholderClient {

    private final JsonPlaceholderClient jsonPlaceholderClient;

    @GetMapping("/users")
    public ResponseEntity<List<JsonPlaceholderUserDTO>> findAllUsers() {
        return jsonPlaceholderClient.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<JsonPlaceholderUserDTO> findUserById(@PathVariable Integer id) {
        return jsonPlaceholderClient.findUserById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<JsonPlaceholderUserDTO> createUser(@RequestBody JsonPlaceholderUserDTO user) {
        var res = jsonPlaceholderClient.createUser(user);
        return ResponseEntity.ok(res.getBody());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<JsonPlaceholderUserDTO> updateUser(@PathVariable Integer id, @RequestBody JsonPlaceholderUserDTO user) {
        var res = jsonPlaceholderClient.updateUser(id, user);
        return ResponseEntity.ok(res.getBody());
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<JsonPlaceholderUserDTO> patchUser(@PathVariable Integer id, @RequestBody JsonPlaceholderUserDTO user) {
        var res = jsonPlaceholderClient.patchUser(id, user);
        return ResponseEntity.ok(res.getBody());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        jsonPlaceholderClient.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos")
    public ResponseEntity<List<JsonPlaceholderTodoDTO>> findAllTodosByUserId(@RequestParam("userId") Integer userId) {
        return jsonPlaceholderClient.findAllTodosByUserId(userId);
    }

}
