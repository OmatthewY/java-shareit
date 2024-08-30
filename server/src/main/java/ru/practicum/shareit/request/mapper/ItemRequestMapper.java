package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest,
                                                   Collection<ItemForRequestDto> itemsForRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsForRequest)
                .build();
    }
}
