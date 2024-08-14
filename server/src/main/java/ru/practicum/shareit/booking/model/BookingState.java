package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String state) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return Optional.of(bookingState);
            }
        }
        return Optional.empty();
    }
}
