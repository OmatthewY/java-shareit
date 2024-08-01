package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemInfoDto> getAllByUsersId(long userId);

    ItemInfoDto getById(long userId, long itemId);

    ItemDto add(long userId, ItemCreateDto itemCreateDto);

    ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    void delete(long itemId, long userId);

    Collection<ItemDto> getAllByText(String text);

    CommentResponseDto addComment(long itemId, long userId, CommentCreateDto commentCreateDto);
}
