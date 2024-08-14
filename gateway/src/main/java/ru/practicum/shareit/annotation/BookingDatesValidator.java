package ru.practicum.shareit.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingItemRequestDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingItemRequestDto> {
    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingItemRequestDto bookingItemRequestDto, ConstraintValidatorContext context) {
        if (bookingItemRequestDto == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        if (bookingItemRequestDto.getStart() == null || bookingItemRequestDto.getEnd() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата бронирования не может быть пустой")
                    .addConstraintViolation();
            return false;
        }

        if (bookingItemRequestDto.getStart().isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата и время начала бронирования не должны быть в прошлом")
                    .addConstraintViolation();
            return false;
        }

        if (bookingItemRequestDto.getEnd().isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата и время окончания бронирования не должны быть в прошлом")
                    .addConstraintViolation();
            return false;
        }

        if (!bookingItemRequestDto.getStart().isBefore(bookingItemRequestDto.getEnd())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала бронирования должна быть раньше даты окончания бронирования")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
