package ru.practicum.shareit.handlers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void constructor_ShouldSetErrorMessage() {
        String message = "Some error occurred";

        ErrorResponse errorResponse = new ErrorResponse(message);

        assertThat(errorResponse.getError()).isEqualTo(message);
    }
}
