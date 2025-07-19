package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeAndDeserialize() throws Exception {
        User user = new User(1L, "Alice", "alice@example.com");

        String json = objectMapper.writeValueAsString(user);
        assertThat(json).contains("\"name\":\"Alice\"");

        User result = objectMapper.readValue(json, User.class);
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
    }
}
