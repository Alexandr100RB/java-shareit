package ru.practicum.shareit.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(final DataNotFoundException e) {
        log.warn("Data not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataAlreadyExistException(final DataAlreadyExistsException e) {
        log.warn("Data already exists: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnhandledException(final Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
