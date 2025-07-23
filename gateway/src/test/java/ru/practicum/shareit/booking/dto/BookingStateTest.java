package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_ShouldReturnCorrectEnum() {
        assertEquals(Optional.of(BookingState.ALL), BookingState.from("ALL"));
        assertEquals(Optional.of(BookingState.CURRENT), BookingState.from("current"));
    }

    @Test
    void from_ShouldReturnEmptyForUnknownState() {
        assertTrue(BookingState.from("unknown").isEmpty());
    }
}
