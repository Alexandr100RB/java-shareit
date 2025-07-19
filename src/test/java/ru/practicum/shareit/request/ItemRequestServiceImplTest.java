package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.CheckConsistencyService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private CheckConsistencyService checkConsistencyService;
    private ItemRequestService itemRequestService;
    private ItemRequestMapper itemRequestMapper;

    private UserDto userDto = new UserDto(1L, "Alex", "alex@alex.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "ItemRequest description",
            userDto, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository,
                checkConsistencyService, null);
    }

    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        when(checkConsistencyService.isUserExistsForValidation(any(Long.class)))
                .thenReturn(true);
        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getItemRequestById(-1L, 1L));
        Assertions.assertEquals("Запрос с id=-1 не найден", exception.getMessage());
    }
}