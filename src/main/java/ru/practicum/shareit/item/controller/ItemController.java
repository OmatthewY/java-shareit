package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemInfoDto> getAllByUsersId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllByUsersId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_ID_HEADER) long userId,
                          @Validated @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.add(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long itemId,
                          @Validated @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.update(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID_HEADER) long userId,
                       @PathVariable long itemId) {
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllByText(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.getAllByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.addComment(itemId, userId, commentCreateDto);
    }
}
