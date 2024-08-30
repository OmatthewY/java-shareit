package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestInfoDto> getAllByUserId(long userId);

    Collection<ItemRequestDto> getAllOtherUsers(long userId, int from, int size);

    ItemRequestInfoDto getById(long userId, long requestId);
}
