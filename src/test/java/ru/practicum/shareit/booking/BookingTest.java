package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeAndDeserialize() throws Exception {
        User booker = new User(1L, "Booker", "booker@example.com");
        Item item = new Item(2L, "Screwdriver", "Tool", true, booker, null);

        Booking booking = new Booking(
                10L,
                LocalDateTime.of(2030, 5, 1, 10, 0),
                LocalDateTime.of(2030, 5, 2, 10, 0),
                item,
                booker,
                Status.APPROVED
        );

        String json = objectMapper.writeValueAsString(booking);
        assertThat(json).contains("\"status\":\"APPROVED\"");

        Booking result = objectMapper.readValue(json, Booking.class);
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(result.getBooker().getEmail()).isEqualTo("booker@example.com");
    }
}
