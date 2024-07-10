package ru.practicum.shareit.exeption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    String message;

    String stackTrace;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
