package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getAllByUserId(Long userId, BookingState state);

    Collection<BookingDto> getAllByOwnerId(Long ownerId, BookingState state);
}
