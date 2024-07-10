package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getAllByUsersId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items запрос");
        Collection<ItemDto> items = itemService.getAllByUsersId(userId);
        log.info("GET /items ответ: запрос выполнен успешно {}", items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.info("GET /items/{} запрос", itemId);
        ItemDto itemDto = itemService.getById(itemId);
        log.info("GET /items/{} ответ: запрос выполнен успешно {}", itemId, itemDto);
        return itemDto;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated @RequestBody ItemDto itemDto) {
        log.info("POST /items запрос: {}", itemDto);
        ItemDto createdItemDto = itemService.add(userId, itemDto);
        log.info("POST /items ответ: запрос выполнен успешно {}", createdItemDto);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} запрос: {}", itemId, itemDto);
        ItemDto updatedItemDto = itemService.update(userId, itemId, itemDto);
        log.info("PATCH /items/{} ответ: запрос выполнен успешно {}", itemId, updatedItemDto);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId) {
        log.info("DELETE /items/{} запрос", itemId);
        itemService.delete(userId, itemId);
        log.info("DELETE /items/{} ответ: запрос выполнен успешно", itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllByText(@RequestParam String text) {
        log.info("GET /items/search?text={} запрос", text);
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<ItemDto> items = itemService.getAllByText(text);
        log.info("GET /items/search?text={} ответ: запрос выполнен успешно {}", text, items.size());
        return items;
    }
}
