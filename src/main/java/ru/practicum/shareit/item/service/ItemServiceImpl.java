package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAllByUsersId(Long userId) {
        checkUserExistence(userId, "GET ALL ITEMS");
        Collection<Item> items = itemRepository.getAllByUsersId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        checkUserExistence(userId, "ADD ITEM");
        Item item = itemRepository.add(userId, ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        checkUserExistence(userId, "UPDATE ITEM");
        Item updatedItem = itemRepository.getById(itemId)
                .orElseThrow(() -> {
                    log.info("UPDATE ITEM Предмет с id = {} не найден", itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });
        Item item = ItemMapper.toItem(itemDto);
        if (!userId.equals(updatedItem.getOwnerId())) {
            throw new NotFoundException("ID пользователя отличается от ID 'владельца'");
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        checkUserExistence(userId, "DELETE ITEM");
        checkItemExistence(itemId, "DELETE ITEM");
        itemRepository.delete(userId, itemId);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> {
                    log.info("GET ITEM BY ID Предмет с id = {} не найден", itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.getAllByText(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkUserExistence(Long userId, String method) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id = {} не найден", method, userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
    }

    private void checkItemExistence(Long itemId, String method) {
        itemRepository.getById(itemId)
                .orElseThrow(() -> {
                    log.info("{} Предмет с id = {} не найден", method, itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });
    }
}
