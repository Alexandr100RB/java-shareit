package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    private JacksonTester<ItemRequestDto> json;
    private Validator validator;
    private ItemRequestDto itemRequestDto;

    public ItemRequestDtoTest(@Autowired JacksonTester<ItemRequestDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);
        itemRequestDto = new ItemRequestDto(
                1L,
                "I need a drill",
                new UserDto(2L, "Alice", "alice@mail.com"),
                LocalDateTime.of(2030, 12, 25, 10, 0),
                List.of(
                        new ItemDto(1L, "Drill", "A powerful drill", true, null, null, null, null, List.of())
                )
        );
    }

    @Test
    void testJsonSerialization() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("I need a drill");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2030-12-25T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("alice@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
    }

    @Test
    void whenDescriptionIsBlankThenValidationFails() {
        itemRequestDto.setDescription("  ");
        Set<jakarta.validation.ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("must not be blank");
    }
}
