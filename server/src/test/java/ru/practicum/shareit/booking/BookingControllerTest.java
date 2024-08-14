package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(BookingDto.Item.builder().id(1L).name("Item").build())
                .booker(BookingDto.Booker.builder().id(1L).build())
                .status("WAITING")
                .build();
    }

    @Test
    void create_ShouldReturnCreatedBooking() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        Mockito.when(bookingService.create(eq(1L), any(BookingCreateDto.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus()));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedBooking() throws Exception {
        bookingDto.setStatus("APPROVED");

        Mockito.when(bookingService.updateStatus(eq(1L), eq(1L), eq(true))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateStatus_ShouldReturnBadRequest_WhenAlreadyApproved() throws Exception {
        Mockito.when(bookingService.updateStatus(eq(1L), eq(1L), eq(true)))
                .thenThrow(new IllegalArgumentException("Нельзя подтвердить бронь, которая уже подтверждена."));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenUserHasNoAccess() throws Exception {
        Mockito.when(bookingService.getById(1L, 1L))
                .thenThrow(new NotFoundException("У вас не найдено такой брони или предмета."));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUserId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(bookingDto);

        Mockito.when(bookingService.getAllByUserId(1L, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getAllByOwnerId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(bookingDto);

        Mockito.when(bookingService.getAllByOwnerId(1L, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getAllByUserId_ShouldReturnEmptyList_WhenNoBookingsFound() throws Exception {
        Mockito.when(bookingService.getAllByUserId(1L, BookingState.ALL)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
