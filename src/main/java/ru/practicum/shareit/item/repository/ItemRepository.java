package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getAllByUsersId(Long userId);

    Item add(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    void delete(Long userId, Long itemId);

    Optional<Item> getById(Long itemId);

    Collection<Item> getAllByText(String text);
}
