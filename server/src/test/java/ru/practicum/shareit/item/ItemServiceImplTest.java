package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.CheckConsistencyService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CheckConsistencyService checker;

    @Mock
    private ItemMapper mapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        now = LocalDateTime.now();

        item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(new ru.practicum.shareit.user.User());
        item.getOwner().setId(10L);

        itemDto = new ItemDto(1L, "Test item", "Description", true, null, null, null, null, null);

        commentDto = new CommentDto(
                1L,
                "Nice item",
                item,
                "UserName",
                LocalDateTime.now()
        );

        comment = new Comment();
        comment.setId(100L);
        comment.setText("Nice item");
        comment.setCreated(now);
        comment.setItem(item);
        comment.setAuthor(item.getOwner());
    }

    @Test
    void getItemById_ShouldReturnExtendedDto_WhenUserIsOwner() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(mapper.toItemExtDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L, 10L);

        assertThat(result).isEqualTo(itemDto);
        verify(mapper).toItemExtDto(item);
    }

    @Test
    void getItemById_ShouldReturnDto_WhenUserIsNotOwner() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L, 20L);

        assertThat(result).isEqualTo(itemDto);
        verify(mapper).toItemDto(item);
    }

    @Test
    void getItemById_ShouldThrow_WhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(1L, 10L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    @Test
    void create_ShouldReturnDto_WhenSuccess() {
        when(checker.isUserExistsForStrictCheck(10L)).thenReturn(true);
        when(mapper.toItem(itemDto, 10L)).thenReturn(item);
        when(repository.save(item)).thenReturn(item);
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.create(itemDto, 10L);

        assertThat(result).isEqualTo(itemDto);
        verify(checker).isUserExistsForStrictCheck(10L);
        verify(repository).save(item);
    }

    @Test
    void getItemsByOwner_ShouldReturnList() {
        Page<Item> page = new PageImpl<>(List.of(item));
        when(checker.isUserExistsForStrictCheck(10L)).thenReturn(true);
        when(repository.findByOwnerId(eq(10L), any(Pageable.class))).thenReturn(page);
        when(mapper.toItemExtDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsByOwner(10L, 0, 10);

        assertThat(result).hasSize(1).containsExactly(itemDto);
    }

    @Test
    void delete_ShouldDelete_WhenOwnerMatches() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        itemService.delete(1L, 10L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenOwnerMismatch() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.delete(1L, 20L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("нет такой вещи");
    }

    @Test
    void getItemsBySearchQuery_ShouldReturnList_WhenTextNotEmpty() {
        Page<Item> page = new PageImpl<>(List.of(item));
        when(repository.getItemsBySearchQuery(eq("test"), any(Pageable.class))).thenReturn(page);
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsBySearchQuery("test", 0, 10);

        assertThat(result).hasSize(1).containsExactly(itemDto);
    }

    @Test
    void getItemsBySearchQuery_ShouldReturnEmpty_WhenTextEmptyOrNull() {
        assertThat(itemService.getItemsBySearchQuery(null, 0, 10)).isEmpty();
        assertThat(itemService.getItemsBySearchQuery("", 0, 10)).isEmpty();
        assertThat(itemService.getItemsBySearchQuery("   ", 0, 10)).isEmpty();
    }

    @Test
    void update_ShouldUpdateFields_WhenOwnerMatches() {
        ItemDto updateDto = new ItemDto(null, "New name", "New desc", false, null, null, null, null, null);

        when(checker.isUserExistsForStrictCheck(10L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.save(any(Item.class))).thenReturn(item);
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.update(updateDto, 10L, 1L);

        assertThat(result).isEqualTo(itemDto);
        assertThat(item.getName()).isEqualTo("New name");
        assertThat(item.getDescription()).isEqualTo("New desc");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void update_ShouldThrow_WhenOwnerMismatch() {
        when(checker.isUserExistsForStrictCheck(10L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(itemDto, 20L, 1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("нет такой вещи");
    }

    @Test
    void createComment_ShouldCreate_WhenBookingExists() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(checker.getBookingWithUserBookedItem(1L, 3L)).thenReturn(new Booking());
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = itemService.createComment(commentDto, 1L, 3L);

        assertThat(result).isEqualTo(commentDto);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_ShouldThrow_WhenNoBooking() {
        when(checker.isUserExistsForStrictCheck(3L)).thenReturn(true);
        when(checker.getBookingWithUserBookedItem(1L, 3L)).thenReturn(null);

        assertThatThrownBy(() -> itemService.createComment(commentDto, 1L, 3L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не бронировал");
    }

    @Test
    void getCommentsByItemId_ShouldReturnList() {
        when(commentRepository.findAllByItemId(1L, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(List.of(comment));
        when(mapper.toCommentDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = itemService.getCommentsByItemId(1L);

        assertThat(result).hasSize(1).containsExactly(commentDto);
    }

    @Test
    void getItemsByRequestId_ShouldReturnList() {
        when(repository.findAllByRequestId(5L, Sort.by(Sort.Direction.DESC, "id")))
                .thenReturn(List.of(item));
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsByRequestId(5L);

        assertThat(result).hasSize(1).containsExactly(itemDto);
    }
}
