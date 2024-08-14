package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private String status;

    @Data
    @Builder
    public static class Item {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class Booker {
        private Long id;
    }
}
