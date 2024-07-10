package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long nextId = 0L;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new IllegalArgumentException("Email должен быть уникальным");
        }
        user.setId(++nextId);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        User user = users.remove(userId);
        if (user != null) {
            emails.remove(user.getEmail());
        }
    }

    @Override
    public void changeEmail(String newEmail, String oldEmail) {
        if (emails.contains(newEmail)) {
            throw new IllegalArgumentException("Email должен быть уникальным");
        }
        emails.remove(oldEmail);
        emails.add(newEmail);
    }
}
