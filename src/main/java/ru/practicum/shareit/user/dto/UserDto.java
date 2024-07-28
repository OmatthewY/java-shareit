package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Имя пользователя не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 30,
            message = "Имя пользователя не может быть длиннее 30 символов")
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Email должен быть действительным")
    @NotBlank(groups = Create.class, message = "Email не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 30,
            message = "Email пользователя не может быть длиннее 30 символов")
    private String email;
}
