package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void getBookings_ShouldReturnOk() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", "ALL")
                        .param("from", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsOwner_ShouldReturnOk() throws Exception {
        when(bookingClient.getBookingsOwner(anyLong(), any(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", "PAST")
                        .param("from", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void create_ShouldReturnOk() throws Exception {
        String json = "{\n" +
                "  \"itemId\": 1,\n" +
                "  \"start\": \"2025-08-01T12:00:00\",\n" +
                "  \"end\": \"2025-08-02T12:00:00\"\n" +
                "}";

        when(bookingClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ShouldReturnOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void update_ShouldReturnOk() throws Exception {
        when(bookingClient.update(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1")
                        .header(HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }
}
