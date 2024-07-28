package ru.practicum.shareit.booking.controller;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingService.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(USER_ID_HEADER) Long ownerId, @PathVariable Long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        BookingState stateEnum = BookingState.from(state);
        if (stateEnum == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getAllByUserId(userId, stateEnum);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        BookingState stateEnum = BookingState.from(state);
        if (stateEnum == null) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingService.getAllByOwnerId(ownerId, stateEnum);
    }
}
