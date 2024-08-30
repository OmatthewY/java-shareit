package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.exeption.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestInfoDto itemRequestInfoDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a bike")
                .created(LocalDateTime.now())
                .build();

        itemRequestInfoDto = ItemRequestInfoDto.builder()
                .id(1L)
                .description("Need a bike")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void create_ShouldReturnCreatedRequest() throws Exception {
        Mockito.when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getAllByUserId_ShouldReturnRequestsForUser() throws Exception {
        List<ItemRequestInfoDto> requests = Collections.singletonList(itemRequestInfoDto);

        Mockito.when(itemRequestService.getAllByUserId(1L)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestInfoDto.getId()));
    }

    @Test
    void getAllByUserId_ShouldReturnEmptyList_WhenNoRequests() throws Exception {
        Mockito.when(itemRequestService.getAllByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllOtherUsers_ShouldReturnRequests() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(itemRequestDto);

        Mockito.when(itemRequestService.getAllOtherUsers(1L, 0, 10)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()));
    }

    @Test
    void getById_ShouldReturnRequest() throws Exception {
        Mockito.when(itemRequestService.getById(1L, 1L)).thenReturn(itemRequestInfoDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestInfoDto.getId()));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenRequestDoesNotExist() throws Exception {
        Mockito.when(itemRequestService.getById(1L, 1L))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
