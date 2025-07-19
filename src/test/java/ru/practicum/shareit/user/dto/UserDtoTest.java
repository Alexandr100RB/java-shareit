package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import jakarta.validation.*;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    private JacksonTester<UserDto> json;
    private UserDto userDto;
    private Validator validator;

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(
                1L,
                "Alex",
                "alex@alex.ru"
        );
    }

    @Test
    void testJsonUserDto() throws Exception {

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alex@alex.ru");
    }

    @Test
    void whenUserDtoIsValidThenViolationsShouldBeEmpty() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }
}