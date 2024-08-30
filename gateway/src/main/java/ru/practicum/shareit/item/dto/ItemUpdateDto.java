package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemUpdateDto {
    @Size(max = 30, message = "Название предмета не может быть длиннее 30 символов")
    private String name;

    @Size(max = 150, message = "Описание предмета не может быть длиннее 150 символов")
    private String description;

    private Boolean available;
}
