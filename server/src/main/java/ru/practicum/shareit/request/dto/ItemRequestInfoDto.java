package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestInfoDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemForRequestDto> items;
}
