package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build());
    }

    @Test
    void testFindAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(user.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(owner.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByItemOwnerIdAndStatusOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindAllByItemOwnerIdOrderByStartDesc() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindUpcomingBookingsByItemId() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findUpcomingBookingsByItemIds(List.of(item.getId()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindLastBookingsByItemId() {
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findLastBookingsByItemIds(List.of(item.getId()));
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst()).isEqualTo(booking);
    }

    @Test
    void testFindConflictingBookings() {
        Booking existingBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThat(conflictingBookings).hasSize(1);
        assertThat(conflictingBookings.getFirst()).isEqualTo(existingBooking);
    }
}
