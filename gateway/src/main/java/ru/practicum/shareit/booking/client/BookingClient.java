package ru.practicum.shareit.booking.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, BookingItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateStatus(Long ownerId, Long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(String.format("/%d", bookingId), userId);
    }

    public ResponseEntity<Object> getAllByUserId(Long userId, BookingState bookingState) {
        Map<String, Object> parameters = Map.of(
                "state", bookingState.name()
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwnerId(Long ownerId, BookingState bookingState) {
        Map<String, Object> parameters = Map.of(
                "state", bookingState.name()
        );
        return get("/owner?state={state}", ownerId, parameters);
    }
}
