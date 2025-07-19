package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    private final JacksonTester<ItemDto> json;
    private Validator validator;
    private ItemDto itemDto;

    public ItemDtoTest(@Autowired JacksonTester<ItemDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);
        itemDto = new ItemDto(
                1L,
                "Screwdriver",
                "Flat-head screwdriver",
                true,
                null,
                5L,
                new BookingShortDto(1L, 2L,
                        LocalDateTime.of(2030, 1, 1, 10, 0),
                        LocalDateTime.of(2030, 1, 2, 10, 0)),
                new BookingShortDto(2L, 3L,
                        LocalDateTime.of(2031, 1, 1, 10, 0),
                        LocalDateTime.of(2031, 1, 2, 10, 0)),
                List.of(new CommentDto(
                        1L,
                        "Nice tool",
                        null, // item поле игнорируется в JSON, ставим null
                        "Bob",
                        LocalDateTime.of(2029, 1, 1, 12, 0)))
        );
    }

    @Test
    void testJsonSerialization() throws Exception {
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Screwdriver");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Flat-head screwdriver");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.startTime").isEqualTo("2030-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.endTime").isEqualTo("2031-01-02T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Nice tool");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Bob");
    }

    @Test
    void whenNameIsBlankThenValidationFails() {
        itemDto.setName(" ");
        Set<jakarta.validation.ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("must not be blank");
    }

    @Test
    void whenDescriptionIsBlankThenValidationFails() {
        itemDto.setDescription("");
        Set<jakarta.validation.ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("must not be blank");
    }

    @Test
    void whenAvailableIsNullThenValidationFails() {
        itemDto.setAvailable(null);
        Set<jakarta.validation.ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("must not be null");
    }
}
