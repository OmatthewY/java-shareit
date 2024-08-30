package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("CREATE-REQUEST Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestInfoDto> getAllByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("GET-ALL-REQUESTS-BY-USER-ID Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        return itemRequestRepository.findAllByRequestorId(userId).stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(request -> getById(userId, request.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllOtherUsers(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("GET-ALL-REQUESTS-OTHER-USERS Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findAllByOrderByCreatedDesc(page).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("GET-REQUEST-BY-ID Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.info("GET-REQUEST-BY-ID Запрос с id={} не найден", userId);
                    return new NotFoundException("Запроса с id=" + userId + " не существует");
                });
        Collection<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemForRequestDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestInfoDto(request, items);
    }
}
