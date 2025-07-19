package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeAndDeserialize() throws IOException {
        User owner = new User(1L, "John", "john@example.com");
        Item item = new Item(1L, "Hammer", "Heavy hammer", true, owner, 5L);

        String json = objectMapper.writeValueAsString(item);
        assertThat(json).contains("\"name\":\"Hammer\"");

        Item result = objectMapper.readValue(json, Item.class);
        assertThat(result.getName()).isEqualTo("Hammer");
        assertThat(result.getOwner().getName()).isEqualTo("John");
    }
}
