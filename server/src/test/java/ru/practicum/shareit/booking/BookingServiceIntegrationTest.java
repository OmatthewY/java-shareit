package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;

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

        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    void testCreateBookingSuccess() {
        User owner2 = userRepository.save(User.builder()
                .name("Item Owner2")
                .email("owner@example2.com")
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("Test Item2")
                .description("Test Description2")
                .available(true)
                .owner(owner2)
                .build());
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item2.getId())
                .build();

        BookingDto createdBooking = bookingService.create(user.getId(), bookingCreateDto);

        assertThat(createdBooking.getBooker().getId()).isEqualTo(user.getId());
        assertThat(createdBooking.getItem().getId()).isEqualTo(item2.getId());
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    }

    @Test
    void testCreateBookingUserNotFound() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.create(999L, bookingCreateDto)
        );
    }

    @Test
    void testCreateBookingItemNotFound() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(999L)
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.create(user.getId(), bookingCreateDto)
        );
    }

    @Test
    void testCreateBookingItemUnavailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.create(user.getId(), bookingCreateDto)
        );
    }

    @Test
    void testCreateBookingConflict() {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.create(user.getId(), bookingCreateDto)
        );
    }

    @Test
    void testCreateBookingByItemOwner() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.create(owner.getId(), bookingCreateDto)
        );
    }

    @Test
    void testUpdateStatusSuccess() {
        BookingDto updatedBooking = bookingService.updateStatus(owner.getId(), booking.getId(), true);

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED.name());
        Booking savedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testUpdateStatusUserNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.updateStatus(999L, booking.getId(), true)
        );
    }

    @Test
    void testUpdateStatusBookingNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.updateStatus(owner.getId(), 999L, true)
        );
    }

    @Test
    void testUpdateStatusUserIsNotOwner() {
        assertThrows(NotFoundException.class, () ->
                bookingService.updateStatus(user.getId(), booking.getId(), true)
        );
    }

    @Test
    void testUpdateStatusBookingAlreadyApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.updateStatus(owner.getId(), booking.getId(), true)
        );
    }

    @Test
    void testGetByIdSuccess() {
        BookingDto bookingDto = bookingService.getById(user.getId(), booking.getId());

        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(user.getId());
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getById(user.getId(), 999L)
        );
    }

    @Test
    void testGetByIdUserNotBookerOrOwner() {
        User anotherUser = userRepository.save(User.builder().name("Another User").email("another@example.com").build());

        assertThrows(NotFoundException.class, () ->
                bookingService.getById(anotherUser.getId(), booking.getId())
        );
    }

    @Test
    void testGetAllByUserIdCurrentBookings() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.CURRENT);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByUserIdPastBookings() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.PAST);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByUserIdFutureBookings() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.FUTURE);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByUserIdWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.WAITING);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByUserIdRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.REJECTED);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByUserIdAllBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByUserId(user.getId(), BookingState.ALL);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdCurrentBookings() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.CURRENT);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdPastBookings() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.PAST);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdFutureBookings() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.FUTURE);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.WAITING);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.REJECTED);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetAllByOwnerIdAllBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingDto> bookings = bookingService.getAllByOwnerId(owner.getId(), BookingState.ALL);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(booking.getId());
    }
}
