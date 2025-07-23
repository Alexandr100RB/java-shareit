package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create_ShouldReturnOk() throws Exception {
        String json = "{\n" +
                "  \"description\": \"Need a drill for one day\"\n" +
                "}";

        when(itemRequestClient.create(any(ItemRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnItemRequests_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getOwnItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}
