package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllByUsersId(Long userId);

    ItemDto add(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    ItemDto getById(Long itemId);

    Collection<ItemDto> getAllByText(String text);
}
