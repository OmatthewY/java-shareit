package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("GET BY ID Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserCreateDto userCreateDto) {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userCreateDto));
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserUpdateDto userUpdateDto) {

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("UPDATE USER Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });

        if (userUpdateDto.getName() != null && !userUpdateDto.getName().isBlank()) {
            userToUpdate.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isBlank()) {
            userToUpdate.setEmail(userUpdateDto.getEmail());
        }
        return UserMapper.INSTANCE.toUserDto(userToUpdate);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        checkUserExistence(userId, "DELETE USER");
        userRepository.deleteById(userId);
    }

    private void checkUserExistence(Long userId, String method) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id = {} не найден", method, userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
    }
}
