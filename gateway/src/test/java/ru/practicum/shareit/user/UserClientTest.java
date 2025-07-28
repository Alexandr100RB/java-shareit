package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientTest {

    private RestTemplate restTemplate;
    private UserClient userClient;

    private static final String BASE_URL = "http://localhost:9090";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);

        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        userClient = new UserClient(BASE_URL, builder);
    }

    @Test
    void create_ShouldCallPost() {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        ResponseEntity<Object> mockResponse = ResponseEntity.ok("Created");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> response = userClient.create(userDto);

        assertEquals(mockResponse, response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(), eq(Object.class));
    }

    @Test
    void getUserById_ShouldCallGet() {
        Long userId = 1L;
        ResponseEntity<Object> mockResponse = ResponseEntity.ok("user data");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertEquals(mockResponse, response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void getUsers_ShouldCallGet() {
        ResponseEntity<Object> mockResponse = ResponseEntity.ok("All Users");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> response = userClient.getUsers();

        assertEquals(mockResponse, response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void update_ShouldCallPatch() {
        UserDto userDto = new UserDto(1L, "Updated", "updated@example.com");
        ResponseEntity<Object> mockResponse = ResponseEntity.ok("Updated");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> response = userClient.update(userDto, 1L);

        assertEquals(mockResponse, response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    void delete_ShouldCallDelete() {
        ResponseEntity<Object> mockResponse = ResponseEntity.ok("Deleted");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> response = userClient.delete(1L);

        assertEquals(mockResponse, response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Object.class));
    }
}
