package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.service = bookingService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) @Positive Long bookerId) {
        log.info("Cоздано бронирование от пользователя с id={}", bookerId);
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
                                        @RequestHeader(USER_ID) @Positive Long userId) {
        return service.getBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) @Positive Long userId) {
        return service.getBookingsOwner(state, userId);
    }
}