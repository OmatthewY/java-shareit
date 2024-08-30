package ru.practicum.shareit.exeption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
    private String error;

    private String stackTrace;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
