package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsByOwner = new HashMap<>();
    private Long nextId = 0L;

    @Override
    public Collection<Item> getAllByUsersId(Long userId) {
        return itemsByOwner.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item add(Long userId, Item item) {
        item.setId(++nextId);
        items.put(item.getId(), item);
        itemsByOwner.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long userId, Long itemId) {
        if (userId.equals(items.get(itemId).getOwnerId())) {
            Item item = items.remove(itemId);
            itemsByOwner.getOrDefault(userId, new ArrayList<>()).remove(item);
            if (itemsByOwner.get(userId).isEmpty()) {
                itemsByOwner.remove(userId);
            }

        }
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getAllByText(String text) {
        String lowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(lowerCase) ||
                        item.getDescription().toLowerCase().contains(lowerCase)) && item.getAvailable())
                .collect(Collectors.toList());
    }
}
