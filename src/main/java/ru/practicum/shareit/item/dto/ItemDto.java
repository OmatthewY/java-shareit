package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(max = 30, message = "Название предмета не может быть длиннее 30 символов")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым")
    @Size(max = 150, message = "Описание предмета не может быть длиннее 150 символов")
    private String description;

    @NotNull(message = "Наличие товара должно быть указано")
    private Boolean available;
}
