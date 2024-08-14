package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void testFindAllByItemId() {
        Comment comment1 = commentRepository.save(Comment.builder()
                .text("Great item!")
                .created(LocalDateTime.now())
                .item(item)
                .author(user)
                .build());

        Comment comment2 = commentRepository.save(Comment.builder()
                .text("Amazing!")
                .created(LocalDateTime.now())
                .item(item)
                .author(user)
                .build());

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo(comment1);
        assertThat(comments.get(1)).isEqualTo(comment2);
    }
}
