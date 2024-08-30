package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();
    }

    @Test
    void getAll_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        var result = userService.getAll();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(1L);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserCreateDto createDto = UserCreateDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        UserDto createdUser = userService.create(createDto);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateUser_WhenValidDataProvided() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Doe")
                .email("janedoe@example.com")
                .build();

        UserDto updatedUser = userService.update(1L, updateDto);

        assertNotNull(updatedUser);
        assertEquals("Jane Doe", updatedUser.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }
}
