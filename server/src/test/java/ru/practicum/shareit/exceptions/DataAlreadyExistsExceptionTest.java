package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Data already exists";
        DataAlreadyExistsException exception = assertThrows(DataAlreadyExistsException.class, () -> {
            throw new DataAlreadyExistsException(message);
        });

        assertThat(exception).hasMessage(message);
    }
}
