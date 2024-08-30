package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemInfoDto> getAllByUsersId(long userId) {
        checkUserExistence(userId, "GET ALL BY USERS ID");

        List<Item> items = itemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsMap = commentRepository.findByItemIn(items).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIds(
                items.stream().map(Item::getId).collect(Collectors.toList()));
        Map<Long, List<Booking>> lastBookingsMap = lastBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsByItemIds(
                items.stream().map(Item::getId).collect(Collectors.toList()));
        Map<Long, List<Booking>> upcomingBookingsMap = upcomingBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items.stream()
                .map(item -> {
                    long itemId = item.getId();
                    BookingForItemDto lastBooking = getLastBookingForItem(itemId, lastBookingsMap);
                    BookingForItemDto nextBooking = getNextBookingForItem(itemId, upcomingBookingsMap);
                    Collection<CommentResponseDto> comments = commentsMap.getOrDefault(itemId, Collections.emptyList())
                            .stream()
                            .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());
                    return ItemMapper.toItemInfoDto(item, lastBooking, nextBooking, userId, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfoDto getById(long userId, long itemId) {
        checkUserExistence(userId, "GET ITEM BY ID");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.info("GET ITEM BY ID Предмет с id = {} не найден", itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });

        List<Booking> allBookings = bookingRepository.findLastBookingsByItemIds(Collections.singletonList(itemId));
        Map<Long, List<Booking>> lastBookingsMap = allBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Booking> upcomingBookings = bookingRepository
                .findUpcomingBookingsByItemIds(Collections.singletonList(itemId));
        Map<Long, List<Booking>> upcomingBookingsMap = upcomingBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        BookingForItemDto lastBooking = getLastBookingForItem(itemId, lastBookingsMap);
        BookingForItemDto nextBooking = getNextBookingForItem(itemId, upcomingBookingsMap);

        Collection<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());

        return ItemMapper.toItemInfoDto(item, lastBooking, nextBooking, userId, comments);
    }

    private BookingForItemDto getLastBookingForItem(long itemId, Map<Long, List<Booking>> lastBookingsMap) {
        List<Booking> lastBookingsForItem = lastBookingsMap.get(itemId);
        return (lastBookingsForItem == null || lastBookingsForItem.isEmpty()) ? null
                : BookingMapper.toBookingForItemDto(lastBookingsForItem.getFirst());
    }

    private BookingForItemDto getNextBookingForItem(long itemId, Map<Long, List<Booking>> upcomingBookingsMap) {
        List<Booking> upcomingBookingsForItem = upcomingBookingsMap.get(itemId);
        return (upcomingBookingsForItem == null || upcomingBookingsForItem.isEmpty()) ? null
                : BookingMapper.toBookingForItemDto(upcomingBookingsForItem.getFirst());
    }

    @Override
    @Transactional
    public ItemDto add(long userId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("ADD ITEM Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(user);

        if (itemCreateDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> {
                        log.info("ADD ITEM Запрос с id = {} не найден", itemCreateDto.getRequestId());
                        return new NotFoundException("Запроса с id = " + itemCreateDto.getRequestId() +
                                " не существует");
                    });
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        checkUserExistence(userId, "UPDATE ITEM");
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.info("UPDATE ITEM Предмет с id = {} не найден", itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });
        if (userId != (updatedItem.getOwner().getId())) {
            throw new NotFoundException("ID пользователя отличается от ID 'владельца'");
        }

        if (itemUpdateDto.getName() != null && !itemUpdateDto.getName().isBlank()) {
            updatedItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null && !itemUpdateDto.getDescription().isBlank()) {
            updatedItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            updatedItem.setAvailable(itemUpdateDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    @Transactional
    public void delete(long itemId, long userId) {
        checkUserExistence(userId, "DELETE ITEM");
        checkItemExistence(itemId, "DELETE ITEM");
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getAllByText(String text) {
        List<Item> items = itemRepository.searchByText(text.toLowerCase());
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(long itemId, long userId, CommentCreateDto commentCreateDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("ADD COMMENT Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.info("ADD COMMENT Предмет с id = {} не найден", itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId,
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        if (!hasBooked) {
            throw new IllegalArgumentException("Вы не можете оставить отзыв на данный предмет.");
        }

        Comment comment = CommentMapper.toComment(commentCreateDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    private void checkUserExistence(Long userId, String method) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id = {} не найден", method, userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
    }

    private void checkItemExistence(Long itemId, String method) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.info("{} Предмет с id = {} не найден", method, itemId);
                    return new NotFoundException("Предмета с id = " + itemId + " не существует");
                });
    }
}
