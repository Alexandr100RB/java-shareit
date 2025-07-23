package ru.practicum.shareit.handlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    static class Dummy {
        public void dummyMethod(Object obj) {}
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument error");
        ErrorResponse response = handler.handleIllegalArgumentException(ex);

        assertNotNull(response);
        assertEquals("Illegal argument error", response.getError());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException("Constraint violation error", null);
        ErrorResponse response = handler.handleConstraintViolationException(ex);

        assertNotNull(response);
        assertEquals("Constraint violation error", response.getError());
    }

    @Test
    void shouldHandleValidationException() {
        ValidationException ex = new ValidationException("Validation exception error");
        ErrorResponse response = handler.handleValidationException(ex);

        assertNotNull(response);
        assertEquals("Validation exception error", response.getError());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() throws NoSuchMethodException {
        Method method = Dummy.class.getMethod("dummyMethod", Object.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorResponse response = handler.handleMethodArgumentNotValidException(ex);

        assertNotNull(response);

        assertTrue(response.getError().contains("Validation"));
    }
}
