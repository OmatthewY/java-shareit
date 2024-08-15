package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateDto userCreateDto) {
        if (userCreateDto == null) {
            return null;
        }
        return User.builder()
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();
    }
}
