package ru.practicum.shareit.item.client;

import ru.practicum.shareit.client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllByUsersId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getById(long userId, long itemId) {
        return get(String.format("/%d", itemId), userId);
    }

    public ResponseEntity<Object> add(long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        return patch(String.format("/%d", itemId), userId, itemUpdateDto);
    }

    public void delete(long itemId, long userId) {
        delete(String.format("/%d", itemId), userId);
    }

    public ResponseEntity<Object> getAllByText(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(long itemId, long userId, CommentDto commentDto) {
        return post(String.format("/%d/comment", itemId), userId, commentDto);
    }
}
