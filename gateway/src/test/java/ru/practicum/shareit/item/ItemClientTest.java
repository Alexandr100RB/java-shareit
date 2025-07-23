package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemClient, "rest");
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldCreateItem() {
        ItemDto itemDto = new ItemDto(
                null,
                "itemName",
                "desc",
                true,
                null,
                null,
                null,
                Collections.emptyList()
        );

        String jsonResponse = """
                {
                    "id":1,
                    "name":"itemName",
                    "description":"desc",
                    "available":true,
                    "requestId":null,
                    "lastBooking":null,
                    "nextBooking":null,
                    "comments":[]
                }
                """;

        server.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.create(1L, itemDto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        server.verify();
    }

    @Test
    void shouldGetItemById() {
        Long userId = 1L;
        Long itemId = 2L;
        String jsonResponse = """
                {
                    "id":2,
                    "name":"itemName2",
                    "description":"desc2",
                    "available":true,
                    "requestId":null,
                    "lastBooking":null,
                    "nextBooking":null,
                    "comments":[]
                }
                """;

        server.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", userId.toString()))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemById(userId, itemId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        server.verify();
    }

    @Test
    void shouldGetItemsByOwner() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        String path = "http://localhost:9090/items?from=" + from + "&size=" + size;
        String jsonResponse = "[]";

        server.expect(requestTo(path))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", userId.toString()))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemsByOwner(userId, from, size);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        server.verify();
    }

    @Test
    void shouldUpdateItem() {
        Long userId = 1L;
        Long itemId = 2L;
        ItemDto itemDto = new ItemDto(itemId, "updatedName", "updatedDesc", true, null, null, null, Collections.emptyList());
        String jsonResponse = """
                {
                    "id":2,
                    "name":"updatedName",
                    "description":"updatedDesc",
                    "available":true,
                    "requestId":null,
                    "lastBooking":null,
                    "nextBooking":null,
                    "comments":[]
                }
                """;

        server.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", userId.toString()))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.update(itemDto, itemId, userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }

    @Test
    void shouldDeleteItem() {
        Long userId = 1L;
        Long itemId = 2L;

        server.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", userId.toString()))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = itemClient.delete(itemId, userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }

    @Test
    void shouldGetItemsBySearchQuery() {
        String text = "searchText";
        int from = 0;
        int size = 10;
        String path = "http://localhost:9090/items/search?text=" + text + "&from=" + from + "&size=" + size;
        String jsonResponse = "[]";

        server.expect(requestTo(path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemsBySearchQuery(text, from, size);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }

    @Test
    void shouldCreateComment() {
        Long userId = 1L;
        Long itemId = 2L;
        CommentDto commentDto = new CommentDto(null, "comment text", null, null);

        String jsonResponse = """
                {
                    "id":1,
                    "text":"comment text"
                }
                """;

        server.expect(requestTo("http://localhost:9090/items/" + itemId + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", userId.toString()))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.createComment(commentDto, itemId, userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }
}
