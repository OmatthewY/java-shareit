package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private final UserService userService;

    @Test
    void testGetAllUsers() throws Exception {
        UserDto user1 = UserDto.builder().id(1L).name("John").email("john@example.com").build();
        UserDto user2 = UserDto.builder().id(2L).name("Jane").email("jane@example.com").build();
        List<UserDto> users = Arrays.asList(user1, user2);

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService).getAll();
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto user = UserDto.builder().id(1L).name("John").email("john@example.com").build();

        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).getById(1L);
    }

    @Test
    void testCreateUser() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder().name("John").email("john@example.com").build();
        UserDto user = UserDto.builder().id(1L).name("John").email("john@example.com").build();

        when(userService.create(any(UserCreateDto.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).create(any(UserCreateDto.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder().name("Johnny").email("johnny@example.com").build();
        UserDto user = UserDto.builder().id(1L).name("Johnny").email("johnny@example.com").build();

        when(userService.update(anyLong(), any(UserUpdateDto.class))).thenReturn(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Johnny\",\"email\":\"johnny@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Johnny"))
                .andExpect(jsonPath("$.email").value("johnny@example.com"));

        verify(userService).update(anyLong(), any(UserUpdateDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}
