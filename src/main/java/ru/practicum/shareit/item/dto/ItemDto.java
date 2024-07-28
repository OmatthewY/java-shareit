package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Название предмета не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 30,
            message = "Название предмета не может быть длиннее 30 символов")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание предмета не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 150,
            message = "Описание предмета не может быть длиннее 150 символов")
    private String description;

    @NotNull(groups = Create.class, message = "Наличие товара должно быть указано")
    private Boolean available;
}
