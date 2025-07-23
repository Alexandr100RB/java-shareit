package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.CheckConsistencyService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemMapperTest {

    private CheckConsistencyService checker;
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        checker = mock(CheckConsistencyService.class);
        itemMapper = new ItemMapper(checker);
    }

    @Test
    void toItemDto_ShouldReturnItemDto_WithComments() {
        Long itemId = 1L;
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(
                itemId,
                "Name",
                "Description",
                true,
                owner,
                3L
        );

        List<CommentDto> comments = List.of(
                new CommentDto(10L, "Nice", item, "Author", LocalDateTime.now())
        );

        when(checker.getCommentsByItemId(itemId)).thenReturn(comments);

        ItemDto result = itemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(comments, result.getComments());

        verify(checker).getCommentsByItemId(itemId);
    }

    @Test
    void toItemDto_ShouldReturnItemDto_WithNullRequestId_WhenRequestIdIsNull() {
        Long itemId = 1L;
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(
                itemId,
                "Name",
                "Description",
                true,
                owner,
                null
        );

        when(checker.getCommentsByItemId(itemId)).thenReturn(List.of());

        ItemDto result = itemMapper.toItemDto(item);

        assertNotNull(result);
        assertNull(result.getRequestId());

        verify(checker).getCommentsByItemId(itemId);
    }

    @Test
    void toItemExtDto_ShouldReturnItemDto_WithBookingsAndComments() {
        Long itemId = 1L;
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(
                itemId,
                "Name",
                "Description",
                true,
                owner,
                3L
        );

        BookingShortDto lastBooking = new BookingShortDto(5L, 10L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        BookingShortDto nextBooking = new BookingShortDto(6L, 11L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        List<CommentDto> comments = List.of(
                new CommentDto(10L, "Nice", item, "Author", LocalDateTime.now())
        );

        when(checker.getLastBooking(itemId)).thenReturn(lastBooking);
        when(checker.getNextBooking(itemId)).thenReturn(nextBooking);
        when(checker.getCommentsByItemId(itemId)).thenReturn(comments);

        ItemDto result = itemMapper.toItemExtDto(item);

        assertNotNull(result);
        assertEquals(lastBooking, result.getLastBooking());
        assertEquals(nextBooking, result.getNextBooking());
        assertEquals(comments, result.getComments());

        verify(checker).getLastBooking(itemId);
        verify(checker).getNextBooking(itemId);
        verify(checker).getCommentsByItemId(itemId);
    }

    @Test
    void toItem_ShouldReturnItem_WithOwnerAndRequestId() {
        Long ownerId = 2L;
        User owner = new User(ownerId, "Owner", "owner@example.com");
        ItemDto itemDto = new ItemDto(1L, "Name", "Description", true, null, 3L, null, null, null);

        when(checker.findUserById(ownerId)).thenReturn(owner);

        Item result = itemMapper.toItem(itemDto, ownerId);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(checker).findUserById(ownerId);
    }

    @Test
    void toItem_ShouldReturnItem_WithNullRequestId_WhenDtoRequestIdIsNull() {
        Long ownerId = 2L;
        User owner = new User(ownerId, "Owner", "owner@example.com");
        ItemDto itemDto = new ItemDto(1L, "Name", "Description", true, null, null, null, null, null);

        when(checker.findUserById(ownerId)).thenReturn(owner);

        Item result = itemMapper.toItem(itemDto, ownerId);

        assertNotNull(result);
        assertNull(result.getRequestId());

        verify(checker).findUserById(ownerId);
    }

    @Test
    void toCommentDto_ShouldReturnCommentDto() {
        User author = new User(3L, "Author", "author@example.com");

        Item item = new Item(
                1L,
                "Name",
                "Description",
                true,
                author,
                3L
        );

        Comment comment = new Comment(10L, "Good item", item, author, LocalDateTime.now());

        CommentDto result = itemMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getItem(), result.getItem());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getCreated(), result.getCreated());
    }
}
