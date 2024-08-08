package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    @Size(max = 30, message = "Имя пользователя не может быть длиннее 30 символов")
    private String name;

    @Email(message = "Email должен быть действительным")
    @Size(max = 30, message = "Email пользователя не может быть длиннее 30 символов")
    private String email;
}
