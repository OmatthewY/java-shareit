package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    Optional<User> getById(Long userId);

    User create(User user);

    User update(Long userId, User user);

    void delete(Long userId);

    void changeEmail(String newEmail, String oldEmail);
}
