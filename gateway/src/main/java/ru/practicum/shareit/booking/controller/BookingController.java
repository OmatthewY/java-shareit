package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody BookingItemRequestDto bookingItemRequestDto) {
        return bookingClient.create(userId, bookingItemRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingClient.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return BookingState.from(state)
                .map(stateEnum -> bookingClient.getAllByUserId(userId, stateEnum))
                .orElseThrow(() -> new ValidationException("Unknown state: " + state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return BookingState.from(state)
                .map(stateEnum -> bookingClient.getAllByOwnerId(ownerId, stateEnum))
                .orElseThrow(() -> new ValidationException("Unknown state: " + state));
    }
}
