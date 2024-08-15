package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(BookingDto.Item.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(BookingDto.Booker.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus().name())
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto == null) {
            return null;
        }
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .build();
    }
}
