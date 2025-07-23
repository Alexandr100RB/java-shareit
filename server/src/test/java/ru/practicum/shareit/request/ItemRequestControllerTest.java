package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemRequestControllerTest {

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ItemRequestDto sampleRequestDto;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        // Регистрируем модуль для поддержки Java 8 времени (LocalDateTime)
        objectMapper.registerModule(new JavaTimeModule());

        sampleRequestDto = new ItemRequestDto(
                1L,
                "Need a drill",
                null,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void create_ShouldReturnCreatedItemRequestDto() throws Exception {
        when(service.create(any(ItemRequestDto.class), eq(1L), any(LocalDateTime.class)))
                .thenReturn(sampleRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto))
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(sampleRequestDto.getDescription()));

        verify(service, times(1))
                .create(any(ItemRequestDto.class), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getItemRequestById_ShouldReturnItemRequestDto() throws Exception {
        when(service.getItemRequestById(1L, 1L)).thenReturn(sampleRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(sampleRequestDto.getDescription()));

        verify(service, times(1)).getItemRequestById(1L, 1L);
    }

    @Test
    void getOwnItemRequests_ShouldReturnListOfItemRequestDto() throws Exception {
        when(service.getOwnItemRequests(1L)).thenReturn(List.of(sampleRequestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(sampleRequestDto.getId()));

        verify(service, times(1)).getOwnItemRequests(1L);
    }

    @Test
    void getAllItemRequests_ShouldReturnListOfItemRequestDto() throws Exception {
        when(service.getAllItemRequests(1L, 0, null)).thenReturn(List.of(sampleRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(sampleRequestDto.getId()));

        verify(service, times(1)).getAllItemRequests(1L, 0, null);
    }
}