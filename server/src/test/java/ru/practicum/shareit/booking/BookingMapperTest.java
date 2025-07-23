package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingMapperTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toBookingDto_shouldMapCorrectly() {
        User user = new User(1L, "User", "user@example.com");
        Item item = new Item(2L, "Item", "Desc", true, user, null);
        Booking booking = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.APPROVED);

        when(itemMapper.toItemDto(item)).thenReturn(any());
        when(userMapper.toUserDto(user)).thenReturn(any());

        BookingDto dto = bookingMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        verify(itemMapper).toItemDto(item);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void toBookingDto_shouldReturnNull_WhenBookingIsNull() {
        assertNull(bookingMapper.toBookingDto(null));
    }

    @Test
    void toBookingShortDto_shouldMapCorrectly() {
        User booker = new User(1L, "Booker", "booker@example.com");
        Booking booking = new Booking(10L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, booker, Status.WAITING);

        BookingShortDto dto = bookingMapper.toBookingShortDto(booking);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getBookerId());
    }

    @Test
    void toBookingShortDto_shouldReturnNull_WhenBookingIsNull() {
        assertNull(bookingMapper.toBookingShortDto(null));
    }

    @Test
    void toBooking_shouldMapCorrectly() {
        BookingInputDto inputDto = new BookingInputDto(5L, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        User booker = new User(3L, "Booker", "booker@example.com");
        Item item = new Item(5L, "Item", "Desc", true, booker, null);

        when(itemService.findItemById(5L)).thenReturn(item);
        when(userService.findUserById(3L)).thenReturn(booker);

        Booking result = bookingMapper.toBooking(inputDto, 3L);

        assertNotNull(result);
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertEquals(Status.WAITING, result.getStatus());
    }
}