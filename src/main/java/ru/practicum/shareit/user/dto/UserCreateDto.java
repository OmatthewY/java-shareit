package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 30, message = "Имя пользователя не может быть длиннее 30 символов")
    private String name;

    @Email(message = "Email должен быть действительным")
    @NotBlank(message = "Email не может быть пустым")
    @Size(max = 30, message = "Email пользователя не может быть длиннее 30 символов")
    private String email;
}
