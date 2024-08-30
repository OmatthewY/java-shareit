package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @Size(max = 150, message = "Комментарий не может быть длиннее 150 символов")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
