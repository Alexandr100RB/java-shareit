package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        BookingShortDto lastBooking = new BookingShortDto(
                1L, 2L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        BookingShortDto nextBooking = new BookingShortDto(
                3L, 4L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        itemDto = new ItemDto(
                1L,
                "дрель",
                "ударная дрель",
                true,
                null,
                lastBooking,
                nextBooking,
                List.of()
        );

        commentDto = new CommentDto(
                1L,
                "Отличная вещь!",
                "Автор комментария",
                LocalDateTime.now()
        );
    }

    @Test
    void shouldGetItemsByOwner() throws Exception {
        when(itemClient.getItemsByOwner(1L, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemClient).getItemsByOwner(1L, 0, 10);
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemClient.create(eq(1L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(1L), any(ItemDto.class));
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemClient.getItemById(1L, 1L))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(1L, 1L);
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemClient.update(any(ItemDto.class), eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).update(any(ItemDto.class), eq(1L), eq(1L));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        when(itemClient.delete(1L, 1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient).delete(1L, 1L);
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(itemClient.getItemsBySearchQuery("дрель", 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemClient).getItemsBySearchQuery("дрель", 0, 10);
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(itemClient.createComment(any(CommentDto.class), eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.ok(commentDto));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).createComment(any(CommentDto.class), eq(1L), eq(1L));
    }
}
