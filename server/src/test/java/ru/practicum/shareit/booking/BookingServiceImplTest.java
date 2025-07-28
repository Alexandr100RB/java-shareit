package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.CheckConsistencyService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private BookingMapper mapper;

    @Mock
    private CheckConsistencyService checker;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingInputDto bookingInputDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();

        bookingInputDto = new BookingInputDto(
                1L,
                now.plusDays(1),
                now.plusDays(2)
        );

        Item item = new Item();
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        item.setId(1L);

        User booker = new User();
        booker.setId(3L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(bookingInputDto.getStart());
        booking.setEnd(bookingInputDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription",
                true, null, null, null, null, null);
        UserDto userDto = new UserDto(3L, "user@example.com", "UserName");

        bookingDto = new BookingDto(
                1L,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                itemDto,
                userDto,
                Status.WAITING
        );
    }

    // Тест для create - успешное создание бронирования
    @Test
    void create_ShouldReturnBookingDto_WhenSuccess() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(checker.isAvailableItem(1L)).thenReturn(true);
        when(mapper.toBooking(bookingInputDto, 3L)).thenReturn(booking);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.create(bookingInputDto, 3L);

        assertThat(result).isEqualTo(bookingDto);
        verify(checker).isUserExistsForStrictCheck(3L);
        verify(checker).isAvailableItem(1L);
        verify(repository).save(booking);
    }

    // create - попытка забронировать свою вещь
    @Test
    void create_ShouldThrow_WhenBookingOwnItem() {
        when(checker.isUserExistsForStrictCheck(2L)).thenReturn(true);
        when(checker.isAvailableItem(1L)).thenReturn(true);

        booking.getItem().getOwner().setId(2L); // владелец = booker
        when(mapper.toBooking(bookingInputDto, 2L)).thenReturn(booking);

        assertThatThrownBy(() -> bookingService.create(bookingInputDto, 2L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("недоступна для бронирования самим владельцем");
    }

    // create - вещь недоступна
    @Test
    void create_ShouldThrow_WhenItemNotAvailable() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(checker.isAvailableItem(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.create(bookingInputDto, 3L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("недоступна для бронирования");
    }

    // update - успешное подтверждение бронирования владельцем
    @Test
    void update_ShouldApproveBooking_WhenOwnerApproves() {
        booking.setStatus(Status.WAITING);
        when(checker.isUserExistsForValidation(2L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(checker.isItemOwner(1L, 2L)).thenReturn(true);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.update(1L, 2L, true);

        assertThat(result).isEqualTo(bookingDto);
        assertThat(booking.getStatus()).isEqualTo(Status.APPROVED);
    }

    // update - владелец отклоняет бронирование
    @Test
    void update_ShouldRejectBooking_WhenOwnerRejects() {
        booking.setStatus(Status.WAITING);
        when(checker.isUserExistsForValidation(2L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(checker.isItemOwner(1L, 2L)).thenReturn(true);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.update(1L, 2L, false);

        assertThat(result).isEqualTo(bookingDto);
        assertThat(booking.getStatus()).isEqualTo(Status.REJECTED);
    }

    // update - бронирующий отменяет бронирование
    @Test
    void update_ShouldCancelBooking_WhenBookerCancels() {
        booking.setStatus(Status.WAITING);
        when(checker.isUserExistsForValidation(3L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.update(1L, 3L, false);

        assertThat(result).isEqualTo(bookingDto);
        assertThat(booking.getStatus()).isEqualTo(Status.CANCELED);
    }

    // update - бронирующий пытается подтвердить бронирование (нельзя)
    @Test
    void update_ShouldThrow_WhenBookerTriesApprove() {
        booking.setStatus(Status.WAITING);
        when(checker.isUserExistsForValidation(3L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(1L, 3L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("только владелец вещи");
    }

    // update - бронирование не найдено
    @Test
    void update_ShouldThrow_WhenBookingNotFound() {
        when(checker.isUserExistsForValidation(3L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(1L, 3L, true))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдено");
    }

    // getBookingById - успешный доступ владельца
    @Test
    void getBookingById_ShouldReturnBookingDto_WhenOwner() {
        when(checker.isUserExistsForStrictCheck(2L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(checker.isItemOwner(1L, 2L)).thenReturn(true);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 2L);

        assertThat(result).isEqualTo(bookingDto);
    }

    // getBookingById - успешный доступ бронирующего
    @Test
    void getBookingById_ShouldReturnBookingDto_WhenBooker() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(checker.isItemOwner(1L, 3L)).thenReturn(false);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 3L);

        assertThat(result).isEqualTo(bookingDto);
    }

    // getBookingById - отказ доступа
    @Test
    void getBookingById_ShouldThrow_WhenUnauthorized() {
        when(checker.isUserExistsForStrictCheck(4L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(checker.isItemOwner(1L, 4L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingById(1L, 4L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("только владелец вещи");
    }

    // getBookingById - бронирование не найдено
    @Test
    void getBookingById_ShouldThrow_WhenBookingNotFound() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(1L, 3L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдено");
    }

    // getBookings - проверка пагинации и вызов метода getPageBookings
    @Test
    void getBookings_ShouldReturnListOfBookingDto() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(now.plusDays(3));
        booking2.setEnd(now.plusDays(4));
        booking2.setItem(booking.getItem());
        booking2.setBooker(booking.getBooker());

        Page<Booking> page = new PageImpl<>(List.of(booking, booking2));
        when(repository.findByBookerId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(mapper.toBookingDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookings("ALL", 3L, 0, 10);

        assertThat(result).hasSize(2);
        verify(repository, atLeastOnce()).findByBookerId(anyLong(), any(Pageable.class));
    }

    // getBookings - невалидный state
    @Test
    void getBookings_ShouldThrow_WhenUnknownState() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getBookings("UNKNOWN", 3L, 0, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Unknown state");
    }

    // getBookingsOwner - проверка вызова getPageBookingsOwner
    @Test
    void getBookingsOwner_ShouldReturnListOfBookingDto() {
        when(checker.isUserExistsForStrictCheck(2L)).thenReturn(true);

        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(repository.findByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(mapper.toBookingDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOwner("ALL", 2L, 0, 10);

        assertThat(result).hasSize(1);
        verify(repository, atLeastOnce()).findByItemOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getLastBooking_ShouldReturnBookingShortDto() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setId(200L);
        booking.setStart(now.minusDays(2));
        booking.setEnd(now.minusDays(1));
        booking.setItem(new Item());
        booking.getItem().setId(itemId);
        booking.setBooker(new User());
        booking.getBooker().setId(20L);

        BookingShortDto expectedDto = new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );

        when(repository.findFirstByItemIdAndEndBeforeOrderByEndDesc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(mapper.toBookingShortDto(booking)).thenReturn(expectedDto);

        BookingShortDto actualDto = bookingService.getLastBooking(itemId);

        assertThat(actualDto).isEqualTo(expectedDto);

        verify(repository).findFirstByItemIdAndEndBeforeOrderByEndDesc(eq(itemId), any(LocalDateTime.class));
        verify(mapper).toBookingShortDto(booking);
    }

    @Test
    void getBookingWithUserBookedItem_ShouldReturnBooking() {
        when(repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                eq(1L),
                eq(3L),
                any(LocalDateTime.class),
                eq(Status.APPROVED)))
                .thenReturn(booking);

        Booking result = bookingService.getBookingWithUserBookedItem(1L, 3L);

        assertThat(result).isEqualTo(booking);
    }

    @Test
    void getBookings_ShouldPageThroughAllPages_WhenSizeIsNull() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(now.plusDays(3));
        booking2.setEnd(now.plusDays(4));
        booking2.setItem(booking.getItem());
        booking2.setBooker(booking.getBooker());

        Page<Booking> page1 = new PageImpl<>(List.of(booking), PageRequest.of(0, 1), 2);
        Page<Booking> page2 = new PageImpl<>(List.of(booking2), PageRequest.of(1, 1), 2);

        when(repository.findByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        when(mapper.toBookingDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookings("ALL", 3L, 0, null);

        assertThat(result).hasSize(2);
        verify(repository, times(2)).findByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsOwner_ShouldPageThroughAllPages_WhenSizeIsNull() {
        when(checker.isUserExistsForStrictCheck(2L)).thenReturn(true);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(now.plusDays(3));
        booking2.setEnd(now.plusDays(4));
        booking2.setItem(booking.getItem());
        booking2.setBooker(booking.getBooker());

        Page<Booking> page1 = new PageImpl<>(List.of(booking), PageRequest.of(0, 1), 2);
        Page<Booking> page2 = new PageImpl<>(List.of(booking2), PageRequest.of(1, 1), 2);

        when(repository.findByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        when(mapper.toBookingDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOwner("ALL", 2L, 0, null);

        assertThat(result).hasSize(2);
        verify(repository, times(2)).findByItemOwnerId(anyLong(), any(Pageable.class));
    }


    @Test
    void getNextBooking_ShouldReturnBookingShortDto() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setId(300L);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));
        booking.setItem(new Item());
        booking.getItem().setId(itemId);
        booking.setBooker(new User());
        booking.getBooker().setId(30L);

        BookingShortDto expectedDto = new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );

        when(repository.findFirstByItemIdAndStartAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(mapper.toBookingShortDto(booking)).thenReturn(expectedDto);

        BookingShortDto actualDto = bookingService.getNextBooking(itemId);

        assertThat(actualDto).isEqualTo(expectedDto);

        verify(repository).findFirstByItemIdAndStartAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class));
        verify(mapper).toBookingShortDto(booking);
    }

    @Test
    void getBookingWithUserBookedItem_ShouldReturnNull_WhenNoBookingFound() {
        when(repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                anyLong(), anyLong(), any(LocalDateTime.class), eq(Status.APPROVED)))
                .thenReturn(null);

        Booking result = bookingService.getBookingWithUserBookedItem(1L, 3L);

        assertThat(result).isNull();
    }
}
