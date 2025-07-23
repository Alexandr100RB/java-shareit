package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CheckConsistencyServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CheckConsistencyService checkConsistencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isUserExistsForStrictCheck_ShouldReturnTrue_WhenUserExists() {
        Long userId = 1L;
        UserDto userDtoMock = mock(UserDto.class);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(userDtoMock);

        boolean result = checkConsistencyService.isUserExistsForStrictCheck(userId);

        assertThat(result).isTrue();
        verify(userService).getUserByIdOrThrow(userId);
    }

    @Test
    void isUserExistsForValidation_ShouldReturnTrue_WhenUserExists() {
        Long userId = 1L;
        UserDto userDtoMock = mock(UserDto.class);
        when(userService.getUserByIdOrValidation(userId)).thenReturn(userDtoMock);

        boolean result = checkConsistencyService.isUserExistsForValidation(userId);

        assertThat(result).isTrue();
        verify(userService).getUserByIdOrValidation(userId);
    }

    @Test
    void isAvailableItem_ShouldReturnItemAvailability() {
        Long itemId = 10L;
        Item item = mock(Item.class);
        when(item.getAvailable()).thenReturn(true);
        when(itemService.findItemById(itemId)).thenReturn(item);

        boolean available = checkConsistencyService.isAvailableItem(itemId);

        assertThat(available).isTrue();
        verify(itemService).findItemById(itemId);
    }

    @Test
    void isItemOwner_ShouldReturnTrue_IfItemBelongsToUser() {
        Long userId = 1L;
        Long itemId = 2L;

        ItemDto itemDtoMock = mock(ItemDto.class);
        when(itemDtoMock.getId()).thenReturn(itemId);

        when(itemService.getItemsByOwner(userId, 0, null))
                .thenReturn(List.of(itemDtoMock));

        boolean result = checkConsistencyService.isItemOwner(itemId, userId);

        assertThat(result).isTrue();
        verify(itemService).getItemsByOwner(userId, 0, null);
    }

    @Test
    void findUserById_ShouldReturnUser() {
        Long userId = 5L;
        User user = mock(User.class);
        when(userService.findUserById(userId)).thenReturn(user);

        User result = checkConsistencyService.findUserById(userId);

        assertThat(result).isEqualTo(user);
        verify(userService).findUserById(userId);
    }

    @Test
    void getLastBooking_ShouldReturnBookingShortDto() {
        Long itemId = 7L;
        BookingShortDto dto = mock(BookingShortDto.class);
        when(bookingService.getLastBooking(itemId)).thenReturn(dto);

        BookingShortDto result = checkConsistencyService.getLastBooking(itemId);

        assertThat(result).isEqualTo(dto);
        verify(bookingService).getLastBooking(itemId);
    }

    @Test
    void getNextBooking_ShouldReturnBookingShortDto() {
        Long itemId = 7L;
        BookingShortDto dto = mock(BookingShortDto.class);
        when(bookingService.getNextBooking(itemId)).thenReturn(dto);

        BookingShortDto result = checkConsistencyService.getNextBooking(itemId);

        assertThat(result).isEqualTo(dto);
        verify(bookingService).getNextBooking(itemId);
    }

    @Test
    void getBookingWithUserBookedItem_ShouldReturnBooking() {
        Long itemId = 7L;
        Long userId = 3L;
        Booking booking = mock(Booking.class);
        when(bookingService.getBookingWithUserBookedItem(itemId, userId)).thenReturn(booking);

        Booking result = checkConsistencyService.getBookingWithUserBookedItem(itemId, userId);

        assertThat(result).isEqualTo(booking);
        verify(bookingService).getBookingWithUserBookedItem(itemId, userId);
    }

    @Test
    void getCommentsByItemId_ShouldReturnCommentsList() {
        Long itemId = 15L;
        List<CommentDto> comments = List.of(mock(CommentDto.class));
        when(itemService.getCommentsByItemId(itemId)).thenReturn(comments);

        List<CommentDto> result = checkConsistencyService.getCommentsByItemId(itemId);

        assertThat(result).isEqualTo(comments);
        verify(itemService).getCommentsByItemId(itemId);
    }
}
