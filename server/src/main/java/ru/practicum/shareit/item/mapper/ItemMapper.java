package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toItemDto(Item item);

    default Item toItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    default ItemInfoDto toItemInfoDto(Item item, BookingForItemDto lastBooking, BookingForItemDto nextBooking,
                                      Long userId, Collection<CommentResponseDto> comments) {
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

    default ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
