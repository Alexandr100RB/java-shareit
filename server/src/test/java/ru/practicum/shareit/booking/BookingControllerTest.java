package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto sampleBookingDto;
    private BookingInputDto sampleBookingInputDto;

    @BeforeEach
    void setUp() {
        sampleBookingInputDto = new BookingInputDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        sampleBookingDto = new BookingDto(
                1L,
                sampleBookingInputDto.getStart(),
                sampleBookingInputDto.getEnd(),
                null, // ItemDto можно добавить если надо
                null, // UserDto можно добавить если надо
                null  // Status можно добавить если надо
        );
    }

    @Test
    void create_ShouldReturnBookingDto() throws Exception {
        Mockito.when(bookingService.create(any(BookingInputDto.class), anyLong()))
                .thenReturn(sampleBookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBookingInputDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleBookingDto.getId()));
    }

    @Test
    void update_ShouldReturnBookingDto() throws Exception {
        Mockito.when(bookingService.update(eq(1L), eq(1L), eq(true)))
                .thenReturn(sampleBookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleBookingDto.getId()));
    }

    @Test
    void getBookingById_ShouldReturnBookingDto() throws Exception {
        Mockito.when(bookingService.getBookingById(eq(1L), eq(1L)))
                .thenReturn(sampleBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleBookingDto.getId()));
    }

    @Test
    void getBookings_ShouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookings(anyString(), eq(1L), anyInt(), nullable(Integer.class)))
                .thenReturn(List.of(sampleBookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleBookingDto.getId()));
    }

    @Test
    void getBookingsOwner_ShouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingsOwner(anyString(), eq(1L), anyInt(), nullable(Integer.class)))
                .thenReturn(List.of(sampleBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleBookingDto.getId()));
    }
}
