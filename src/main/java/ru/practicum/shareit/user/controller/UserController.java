package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("GET /users запрос");
        Collection<UserDto> users = userService.getAll();
        log.info("GET /users ответ: запрос выполнен успешно {}", users.size());
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("GET /users/{} запрос", userId);
        UserDto userDto = userService.getById(userId);
        log.info("GET /users/{} ответ: запрос выполнен успешно {}", userId, userDto);
        return userDto;
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("POST /users запрос: {}", userDto);
        UserDto createdUserDto = userService.create(userDto);
        log.info("POST /users ответ: запрос выполнен успешно {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} запрос: {}", userId, userDto);
        UserDto updateUserDto = userService.update(userId, userDto);
        log.info("PATCH /users/{} ответ: запрос выполнен успешно {}", userId, updateUserDto);
        return updateUserDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE /users/{} запрос", userId);
        userService.delete(userId);
        log.info("DELETE /users/{} ответ: запрос выполнен успешно", userId);
    }
}
