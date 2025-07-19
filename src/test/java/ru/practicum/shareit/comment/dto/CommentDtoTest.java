package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    private final JacksonTester<CommentDto> json;
    private Validator validator;
    private CommentDto commentDto;

    public CommentDtoTest(@Autowired JacksonTester<CommentDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);
        commentDto = new CommentDto(
                1L,
                "This is a comment",
                null,  // item игнорируется в JSON
                "Alice",
                LocalDateTime.of(2030, 6, 15, 14, 30)
        );
    }

    @Test
    void testJsonSerialization() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("This is a comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2030-06-15T14:30:00");
        assertThat(result).doesNotHaveJsonPath("$.item");  // item игнорируется
    }

    @Test
    void whenTextIsBlankThenValidationFails() {
        commentDto.setText(" ");
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("must not be blank");
    }
}
