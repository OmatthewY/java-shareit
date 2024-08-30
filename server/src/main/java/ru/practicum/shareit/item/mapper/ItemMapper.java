package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemCreateDto itemCreateDto) {
        if (itemCreateDto == null) {
            return null;
        }
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, BookingForItemDto lastBooking, BookingForItemDto nextBooking,
                                            Long userId, Collection<CommentResponseDto> comments) {
        if (item == null) {
            return null;
        }
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(item.getOwner().getId().equals(userId) ? lastBooking : null)
                .nextBooking(item.getOwner().getId().equals(userId) ? nextBooking : null)
                .comments(comments)
                .build();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
