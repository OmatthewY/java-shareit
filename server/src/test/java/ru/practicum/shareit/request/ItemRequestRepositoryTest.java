package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    @Test
    public void testFindAllByRequestorId() {
        User user = userRepository.save(User.builder()
                .name("John")
                .email("john@example.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Need an item")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user.getId());

        assertThat(requests).isNotEmpty();
    }

    @Test
    public void testFindAllByOrderByCreatedDesc() {
        User user = userRepository.save(User.builder()
                .name("John")
                .email("john@example.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Need an item")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        Page<ItemRequest> requests = itemRequestRepository.findAllByOrderByCreatedDesc(PageRequest.of(0, 10));

        assertThat(requests.getContent()).isNotEmpty();
    }
}
