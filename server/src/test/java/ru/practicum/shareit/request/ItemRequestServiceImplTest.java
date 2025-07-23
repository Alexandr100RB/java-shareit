package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.CheckConsistencyService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private CheckConsistencyService checker;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private final Long requestorId = 1L;
    private final Long otherUserId = 2L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User(1L, "John Doe", "john@example.com");
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");
        itemRequest = new ItemRequest(1L, "description", user, now);
        itemRequestDto = new ItemRequestDto(1L, "description", userDto, now, null);
    }

    @Test
    void create_ShouldReturnDto_WhenSuccess() {
        when(mapper.toItemRequest(itemRequestDto, requestorId, now)).thenReturn(itemRequest);
        when(repository.save(itemRequest)).thenReturn(itemRequest);
        when(mapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = service.create(itemRequestDto, requestorId, now);

        assertThat(result).isEqualTo(itemRequestDto);
        verify(repository).save(itemRequest);
    }

    @Test
    void getItemRequestById_ShouldReturnDto_WhenFound() {
        when(checker.isUserExistsForValidation(requestorId)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(mapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = service.getItemRequestById(1L, requestorId);

        assertThat(result).isEqualTo(itemRequestDto);
        verify(checker).isUserExistsForValidation(requestorId);
        verify(repository).findById(1L);
    }

    @Test
    void getItemRequestById_ShouldThrow_WhenNotFound() {
        when(checker.isUserExistsForValidation(requestorId)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getItemRequestById(1L, requestorId))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Запрос с id=1 не найден");
    }

    @Test
    void getOwnItemRequests_ShouldReturnList() {
        when(checker.isUserExistsForValidation(requestorId)).thenReturn(true);
        when(repository.findAllByRequestorId(requestorId, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(List.of(itemRequest));
        when(mapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = service.getOwnItemRequests(requestorId);

        assertThat(result).containsExactly(itemRequestDto);
        verify(checker).isUserExistsForValidation(requestorId);
    }

    @Test
    void getAllItemRequests_ShouldReturnPagedList_WhenSizeIsNotNull() {
        when(checker.isUserExistsForValidation(otherUserId)).thenReturn(true);

        Page<ItemRequest> page1 = new PageImpl<>(List.of(itemRequest));
        when(repository.findAllByRequestorIdNot(eq(otherUserId), any(Pageable.class))).thenReturn(page1);

        when(mapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = service.getAllItemRequests(otherUserId, 0, 10);

        assertThat(result).contains(itemRequestDto);
        verify(checker).isUserExistsForValidation(otherUserId);
        verify(repository).findAllByRequestorIdNot(eq(otherUserId), any(Pageable.class));
    }

    @Test
    void getAllItemRequests_ShouldReturnList_WhenSizeIsNull() {
        when(checker.isUserExistsForValidation(otherUserId)).thenReturn(true);
        when(repository.findAllByRequestorIdNotOrderByCreatedDesc(otherUserId))
                .thenReturn(List.of(itemRequest));
        when(mapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = service.getAllItemRequests(otherUserId, 0, null);

        assertThat(result).contains(itemRequestDto);
        verify(checker).isUserExistsForValidation(otherUserId);
        verify(repository).findAllByRequestorIdNotOrderByCreatedDesc(otherUserId);
    }
}
