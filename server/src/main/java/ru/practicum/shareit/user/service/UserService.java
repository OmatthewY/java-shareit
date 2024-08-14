package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getById(long userId);

    UserDto create(UserCreateDto userCreateDto);

    UserDto update(long userId, UserUpdateDto userUpdateDto);

    void delete(long id);
}
