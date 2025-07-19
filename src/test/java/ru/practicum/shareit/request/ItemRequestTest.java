package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeItemRequest() throws Exception {
        ItemRequest request = new ItemRequest(
                1L,
                "Looking for a cordless drill",
                new User(10L, "Alice", "alice@example.com"),
                LocalDateTime.of(2030, 12, 25, 15, 30)
        );

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Looking for a cordless drill\"");
        assertThat(json).contains("\"created\":\"2030-12-25T15:30:00\"");
        assertThat(json).contains("\"requestor\"");
        assertThat(json).contains("\"name\":\"Alice\"");
        assertThat(json).contains("\"email\":\"alice@example.com\"");
    }

    @Test
    void shouldDeserializeItemRequest() throws Exception {
        String json = """
            {
              "id": 1,
              "description": "Looking for a cordless drill",
              "requestor": {
                "id": 10,
                "name": "Alice",
                "email": "alice@example.com"
              },
              "created": "2030-12-25T15:30:00"
            }
            """;

        ItemRequest request = objectMapper.readValue(json, ItemRequest.class);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Looking for a cordless drill");
        assertThat(request.getRequestor()).isNotNull();
        assertThat(request.getRequestor().getId()).isEqualTo(10L);
        assertThat(request.getRequestor().getName()).isEqualTo("Alice");
        assertThat(request.getRequestor().getEmail()).isEqualTo("alice@example.com");
        assertThat(request.getCreated()).isEqualTo(LocalDateTime.of(2030, 12, 25, 15, 30));
    }
}
