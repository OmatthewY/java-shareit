package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    @NotNull(message = "Начало бронирования не может быть пустым")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Конец бронирования не может быть пустым")
    @Future(message = "Дата конца бронирования не может быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "ID предмета не может быть пустым")
    private Long itemId;
}
