package ru.practicum.shareit.handlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldStoreErrorMessage() {
        ErrorResponse response = new ErrorResponse("Ошибка");
        assertEquals("Ошибка", response.getError());
    }
}
