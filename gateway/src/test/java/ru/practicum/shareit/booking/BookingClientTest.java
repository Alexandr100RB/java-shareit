package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        when(builder.uriTemplateHandler(any(DefaultUriBuilderFactory.class))).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient("http://localhost:9090", builder);
    }

    @Test
    void shouldCreateBooking() {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        ResponseEntity<Object> expected = ResponseEntity.ok("ok");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(Object.class)
        )).thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.create(1L, requestDto);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void shouldGetBookingById() {
        ResponseEntity<Object> expected = ResponseEntity.ok("booking");

        when(restTemplate.exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                any(), // любой HttpEntity
                eq(Object.class)
        )).thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBooking(1L, 1L);

        assertThat(response).isEqualTo(expected);
    }


    @Test
    void shouldGetBookings() {
        ResponseEntity<Object> expected = ResponseEntity.ok("list");

        when(restTemplate.exchange(
                contains("?state=ALL&from=0"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBookings(1L, BookingState.ALL, 0, 10);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void shouldGetOwnerBookings() {
        ResponseEntity<Object> expected = ResponseEntity.ok("ownerBookings");

        when(restTemplate.exchange(
                contains("/owner?state=ALL&from=0"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBookingsOwner(1L, BookingState.ALL, 0, 10);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void shouldUpdateBookingStatus() {
        ResponseEntity<Object> expected = ResponseEntity.ok("updated");

        when(restTemplate.exchange(
                contains("?approved=true"),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class)
        )).thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.update(1L, 1L, true);

        assertThat(response).isEqualTo(expected);
    }
}