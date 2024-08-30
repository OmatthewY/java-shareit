package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    private User user;
    private User owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("Item Owner")
                .email("owner@example.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build());

        request = requestRepository.save(ItemRequest.builder()
                .description("Test Request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void testGetAllByUsersId() {
        Collection<ItemInfoDto> items = itemService.getAllByUsersId(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getId()).isEqualTo(item.getId());
    }

    @Test
    void testGetById() {
        ItemInfoDto itemInfoDto = itemService.getById(owner.getId(), item.getId());
        assertThat(itemInfoDto.getId()).isEqualTo(item.getId());
    }

    @Test
    void testCreateItem() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDto createdItem = itemService.add(owner.getId(), itemCreateDto);

        assertThat(createdItem.getName()).isEqualTo("New Item");
        assertThat(createdItem.getDescription()).isEqualTo("New Description");
    }

    @Test
    void testCreateItemRequestNotFound() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .requestId(999L)
                .build();

        assertThrows(NotFoundException.class, () ->
                itemService.add(owner.getId(), itemCreateDto)
        );
    }

    @Test
    void testUpdateItem() {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.update(owner.getId(), item.getId(), itemUpdateDto);

        assertThat(updatedItem.getName()).isEqualTo("Updated Item");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void testUpdateItemNotFound() {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        assertThrows(NotFoundException.class, () ->
                itemService.update(owner.getId(), 999L, itemUpdateDto)
        );
    }

    @Test
    void testDeleteItem() {
        itemService.delete(item.getId(), owner.getId());
        assertThrows(NotFoundException.class, () ->
                itemService.getById(owner.getId(), item.getId())
        );
    }

    @Test
    void testGetAllByText() {
        Collection<ItemDto> items = itemService.getAllByText("Test");
        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getId()).isEqualTo(item.getId());
    }

    @Test
    void testAddCommentSuccess() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great Item")
                .build();

        CommentResponseDto createdComment = itemService.addComment(item.getId(), user.getId(), commentCreateDto);

        assertThat(createdComment.getText()).isEqualTo("Great Item");
        assertThat(createdComment.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void testAddCommentWithoutBooking() {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great Item")
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                itemService.addComment(item.getId(), user.getId(), commentCreateDto)
        );
    }
}
