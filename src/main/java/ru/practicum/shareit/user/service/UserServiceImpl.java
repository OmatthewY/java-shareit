package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
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
        Collection<User> users = userRepository.getAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("GET Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {

        User userToUpdate = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("UPDATE USER Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        User user = UserMapper.toUser(userDto);

        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()
                && !userToUpdate.getEmail().equals(user.getEmail())) {
            userRepository.changeEmail(user.getEmail(), userToUpdate.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public void delete(long userId) {
        checkUserExistence(userId, "DELETE USER");
        userRepository.delete(userId);
    }

    private void checkUserExistence(Long userId, String method) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id = {} не найден", method, userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
    }
}
