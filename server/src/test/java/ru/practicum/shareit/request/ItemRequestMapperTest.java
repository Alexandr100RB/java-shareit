package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestMapperTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toItemRequestDto_shouldMapCorrectly() {
        User user = new User(1L, "user", "user@mail.com");
        ItemRequest request = new ItemRequest(10L, "need hammer", user, LocalDateTime.now());
        UserDto userDto = new UserDto(1L, "user", "user@mail.com");

        when(userMapper.toUserDto(user)).thenReturn(userDto);
        when(itemService.getItemsByRequestId(10L)).thenReturn(Collections.emptyList());

        ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("need hammer", dto.getDescription());
        verify(userMapper).toUserDto(user);
        verify(itemService).getItemsByRequestId(10L);
    }

    @Test
    void toItemRequest_shouldMapCorrectly() {
        User requestor = new User(1L, "name", "mail");
        ItemRequestDto dto = new ItemRequestDto(null, "desc", null, null, null);

        when(userService.findUserById(1L)).thenReturn(requestor);
        LocalDateTime created = LocalDateTime.now();

        ItemRequest result = itemRequestMapper.toItemRequest(dto, 1L, created);

        assertNotNull(result);
        assertEquals("desc", result.getDescription());
        assertEquals(created, result.getCreated());
        assertEquals(requestor, result.getRequestor());
    }
}