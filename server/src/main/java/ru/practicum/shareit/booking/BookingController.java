package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.service = bookingService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) @Positive Long bookerId) {
        log.info("Создано бронирования от пользователя с id={}", bookerId);
        return service.create(bookingInputDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) @Positive Long userId, @RequestParam Boolean approved) {
        log.info("Обновлён статус бронирования с id={}", bookingId);
        return service.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable @Positive Long bookingId,
                                     @RequestHeader(USER_ID) @Positive Long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) @Positive Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(required = false) Integer size) {
        return service.getBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) @Positive Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(required = false) Integer size) {
        return service.getBookingsOwner(state, userId, from, size);
    }
}