package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        return userClient.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                          @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userClient.update(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userClient.delete(userId);
    }
}
