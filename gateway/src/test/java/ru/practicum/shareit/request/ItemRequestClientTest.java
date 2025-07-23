package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    private static final String BASE_URL = "http://localhost:9090/requests";

    @Test
    void create_ShouldCallPost() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto(1L, "desc", null, null, null);
        String jsonResponse = "\"created\"";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.create(requestDto, userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("created");

        mockServer.verify();
    }

    @Test
    void getItemRequestById_ShouldCallGet() {
        Long userId = 1L;
        Long requestId = 2L;
        String jsonResponse = "\"request data\"";

        mockServer.expect(requestTo(BASE_URL + "/" + requestId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getItemRequestById(userId, requestId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("request data");

        mockServer.verify();
    }

    @Test
    void getOwnItemRequests_ShouldCallGet() {
        Long userId = 1L;
        String jsonResponse = "\"own requests\"";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getOwnItemRequests(userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("own requests");

        mockServer.verify();
    }

    @Test
    void getAllItemRequests_ShouldCallGet_WithPagination() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String path = BASE_URL + "/all?from=" + from + "&size=" + size;
        String jsonResponse = "\"all requests\"";

        mockServer.expect(requestTo(path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAllItemRequests(userId, from, size);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("all requests");

        mockServer.verify();
    }

    @Test
    void getAllItemRequests_ShouldCallGet_WithoutSize() {
        Long userId = 1L;
        int from = 5;
        String path = BASE_URL + "/all?from=" + from;
        String jsonResponse = "\"all requests without size\"";

        mockServer.expect(requestTo(path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAllItemRequests(userId, from, null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("all requests without size");

        mockServer.verify();
    }
}
