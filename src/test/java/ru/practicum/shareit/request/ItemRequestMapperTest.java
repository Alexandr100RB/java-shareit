package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestMapper mapper;

    private User user;
    private UserDto userDto;
    private ItemRequest request;
    private ItemRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.of(2030, 1, 1, 10, 0);
        user = new User(1L, "John", "john@example.com");
        userDto = new UserDto(1L, "John", "john@example.com");

        request = new ItemRequest(1L, "Need a drill", user, now);
        requestDto = new ItemRequestDto(1L, "Need a drill", userDto, now, List.of());
    }

    @Test
    void toItemRequestDto_shouldMapCorrectly() {
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        when(itemService.getItemsByRequestId(1L)).thenReturn(List.of());

        ItemRequestDto result = mapper.toItemRequestDto(request);

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getCreated()).isEqualTo(request.getCreated());
        assertThat(result.getRequestor()).isEqualTo(userDto);
        assertThat(result.getItems()).isEmpty();

        verify(userMapper).toUserDto(user);
        verify(itemService).getItemsByRequestId(1L);
    }

    @Test
    void toItemRequest_shouldMapCorrectly() {
        when(userService.findUserById(1L)).thenReturn(user);

        ItemRequest result = mapper.toItemRequest(requestDto, 1L, now);

        assertThat(result.getId()).isNull();
        assertThat(result.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(result.getRequestor()).isEqualTo(user);
        assertThat(result.getCreated()).isEqualTo(now);

        verify(userService).findUserById(1L);
    }
}
