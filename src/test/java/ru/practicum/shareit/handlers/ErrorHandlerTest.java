package ru.practicum.shareit.handlers;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleDataNotFoundException_shouldReturnErrorResponseWithMessage() {
        DataNotFoundException ex = new DataNotFoundException("Not found");
        ErrorResponse response = errorHandler.handleDataNotFoundException(ex);

        assertThat(response.getError()).isEqualTo("Not found");
    }

    @Test
    void handleValidationException_shouldReturnErrorResponseWithMessage() {
        ValidationException ex = new ValidationException("Validation failed");
        ErrorResponse response = errorHandler.handleValidationException(ex);

        assertThat(response.getError()).isEqualTo("Validation failed");
    }

    @Test
    void handleDataAlreadyExistException_shouldReturnErrorResponseWithMessage() {
        DataAlreadyExistsException ex = new DataAlreadyExistsException("Already exists");
        ErrorResponse response = errorHandler.handleDataAlreadyExistException(ex);

        assertThat(response.getError()).isEqualTo("Already exists");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnErrorResponseWithMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        // Задаём фейковое сообщение
        org.mockito.Mockito.when(ex.getMessage()).thenReturn("Method argument is invalid");

        ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(ex);

        assertThat(response.getError()).isEqualTo("Method argument is invalid");
    }

    @Test
    void handleConstraintViolationException_shouldReturnErrorResponseWithMessage() {
        ConstraintViolationException ex = new ConstraintViolationException("Constraint violated", null);
        ErrorResponse response = errorHandler.handleConstraintViolationException(ex);

        assertThat(response.getError()).isEqualTo("Constraint violated");
    }

    @Test
    void handleUnhandledException_shouldReturnErrorResponseWithMessage() {
        Exception ex = new RuntimeException("Unexpected");
        ErrorResponse response = errorHandler.handleUnhandledException(ex);

        assertThat(response.getError()).isEqualTo("Unexpected");
    }
}
