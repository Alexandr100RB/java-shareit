package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.nullable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto sampleItemDto;
    private CommentDto sampleCommentDto;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");

        sampleItemDto = new ItemDto(
                1L,
                "Дрель",
                "Мощная дрель",
                true,
                owner,
                null,
                null,
                null,
                new ArrayList<>()
        );

        sampleCommentDto = new CommentDto(
                1L,
                "Отличная вещь!",
                null,
                "Пользователь",
                LocalDateTime.now()
        );
    }

    @Test
    void getItemById_ShouldReturnItemDto() throws Exception {
        Mockito.when(itemService.getItemById(eq(1L), eq(1L))).thenReturn(sampleItemDto);

        mockMvc.perform(get("/items/1")
                        .header(OWNER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleItemDto.getId()))
                .andExpect(jsonPath("$.name").value(sampleItemDto.getName()));
    }

    @Test
    void create_ShouldReturnCreatedItemDto() throws Exception {
        Mockito.when(itemService.create(any(ItemDto.class), eq(1L))).thenReturn(sampleItemDto);

        mockMvc.perform(post("/items")
                        .header(OWNER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleItemDto.getId()))
                .andExpect(jsonPath("$.name").value(sampleItemDto.getName()));
    }

    @Test
    void getItemsByOwner_ShouldReturnList() throws Exception {
        Mockito.when(itemService.getItemsByOwner(eq(1L), anyInt(), nullable(Integer.class)))
                .thenReturn(List.of(sampleItemDto));

        mockMvc.perform(get("/items")
                        .header(OWNER_HEADER, 1L)
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleItemDto.getId()));
    }

    @Test
    void update_ShouldReturnUpdatedItemDto() throws Exception {
        Mockito.when(itemService.update(any(ItemDto.class), eq(1L), eq(1L))).thenReturn(sampleItemDto);

        mockMvc.perform(patch("/items/1")
                        .header(OWNER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleItemDto.getId()));
    }

    @Test
    void delete_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(itemService).delete(eq(1L), eq(1L));

        mockMvc.perform(delete("/items/1")
                        .header(OWNER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsBySearchQuery_ShouldReturnList() throws Exception {
        Mockito.when(itemService.getItemsBySearchQuery(eq("дрель"), anyInt(), nullable(Integer.class)))
                .thenReturn(List.of(sampleItemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleItemDto.getId()));
    }

    @Test
    void createComment_ShouldReturnCommentDto() throws Exception {
        Mockito.when(itemService.createComment(any(CommentDto.class), eq(1L), eq(1L)))
                .thenReturn(sampleCommentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(OWNER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(sampleCommentDto.getText()));
    }
}
