package ru.practicum.shareit.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleDataNotFoundException_ShouldReturnErrorResponse() {
        String message = "User not found";
        DataNotFoundException ex = new DataNotFoundException(message);

        ErrorResponse response = errorHandler.handleDataNotFoundException(ex);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(message);
    }

    @Test
    void handleDataAlreadyExistException_ShouldReturnErrorResponse() {
        String message = "Email already used";
        DataAlreadyExistsException ex = new DataAlreadyExistsException(message);

        ErrorResponse response = errorHandler.handleDataAlreadyExistException(ex);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(message);
    }

    @Test
    void handleUnhandledException_ShouldReturnErrorResponse() {
        String message = "Unknown error";
        Exception ex = new Exception(message);

        ErrorResponse response = errorHandler.handleUnhandledException(ex);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(message);
    }
}