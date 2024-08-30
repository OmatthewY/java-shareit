package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.annotation.ValidBookingDates;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidBookingDates
public class BookingItemRequestDto {
	@NotNull(message = "ID предмета не может быть пустым")
	private long itemId;

	@NotNull(message = "Начало бронирования не может быть пустым")
	@FutureOrPresent(message = "Дата начала бронирования не может быть в будущем")
	private LocalDateTime start;

	@NotNull(message = "Конец бронирования не может быть пустым")
	@Future(message = "Дата конца бронирования не может быть в будущем")
	private LocalDateTime end;
}
