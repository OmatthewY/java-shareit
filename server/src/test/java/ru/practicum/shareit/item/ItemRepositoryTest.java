package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());
    }

    @Test
    void testFindAllByOwnerId() {
        Item item1 = itemRepository.save(Item.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .owner(user)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("Item2")
                .description("Description2")
                .available(true)
                .owner(user)
                .build());

        Collection<Item> items = itemRepository.findAllByOwnerId(user.getId());

        assertThat(items).hasSize(2);
        assertThat(items).containsExactlyInAnyOrder(item1, item2);
    }

    @Test
    void testFindById() {
        Item item = itemRepository.save(Item.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .owner(user)
                .build());

        Optional<Item> foundItem = itemRepository.findById(item.getId());

        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getId()).isEqualTo(item.getId());
    }

    @Test
    void testSearchByText() {
        Item item = itemRepository.save(Item.builder()
                .name("TestItem")
                .description("A description of the item")
                .available(true)
                .owner(user)
                .build());

        List<Item> items = itemRepository.searchByText("TestItem");

        assertThat(items).hasSize(1);
        assertThat(items.getFirst()).isEqualTo(item);
    }

    @Test
    void testDeleteByIdAndOwnerId() {
        Item item = itemRepository.save(Item.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .owner(user)
                .build());

        itemRepository.deleteByIdAndOwnerId(item.getId(), user.getId());

        Optional<Item> deletedItem = itemRepository.findById(item.getId());
        assertThat(deletedItem).isNotPresent();
    }
}
