package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new ItemRequestDto(
                1L,
                "Need a screwdriver",
                new UserDto(1L, "John", "john@example.com"),
                LocalDateTime.of(2030, 1, 1, 10, 0),
                List.of()
        );
    }

    @Test
    void createItemRequest() throws Exception {
        Mockito.when(service.create(any(), eq(1L), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getItemRequestById() throws Exception {
        Mockito.when(service.getItemRequestById(1L, 1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getOwnItemRequests() throws Exception {
        Mockito.when(service.getOwnItemRequests(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }

    @Test
    void getAllItemRequests() throws Exception {
        Mockito.when(service.getAllItemRequests(1L, 0, null)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }
}
