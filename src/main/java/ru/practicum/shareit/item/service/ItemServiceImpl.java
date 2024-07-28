package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Collection<ItemInfoDto> getAllByUsersId(long userId) {
        checkUserExistence(userId, "GET ALL BY USERS ID");
        return itemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .map(item -> getById(userId, item.getId()))
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
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemId(itemId);
        BookingForItemDto lastBooking = lastBookings.isEmpty() ? null :
                BookingMapper.INSTANCE.toBookingForItemDto(lastBookings.getFirst());

        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsByItemId(itemId);
        BookingForItemDto nextBooking = upcomingBookings.isEmpty() ? null :
                BookingMapper.INSTANCE.toBookingForItemDto(upcomingBookings.getFirst());

        Collection<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper.INSTANCE::toCommentDto).collect(Collectors.toList());

        return ItemMapper.INSTANCE.toItemInfoDto(item, lastBooking, nextBooking, userId, comments);
    }

    @Override
    @Transactional
    public ItemDto add(long userId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("ADD ITEM Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователя с id = " + userId + " не существует");
                });
        Item item = ItemMapper.INSTANCE.toItem(itemCreateDto);
        item.setOwner(user);
        return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
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
        return ItemMapper.INSTANCE.toItemDto(itemRepository.save(updatedItem));
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
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
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

        List<Booking> endedBookings = bookingRepository.findLastBookingsByItemId(itemId);

        String text = commentDto.getText();
        if (text.isEmpty()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }

        for (Booking booking : endedBookings) {
            if (booking.getBooker().getId().equals(userId)) {
                Comment comment = Comment.builder()
                        .text(text)
                        .item(item)
                        .author(author)
                        .created(LocalDateTime.now())
                        .build();
                return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
            }
        }

        throw new ValidationException("Вы не можете оставить отзыв на данный предмет.");
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
