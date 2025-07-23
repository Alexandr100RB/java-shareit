package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "John Doe", "john@example.com");
        userDto = new UserDto(1L, "John Doe", "john@example.com");
    }

    @Test
    void getUsers_ShouldReturnListOfUserDtos() {
        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getUsers();

        assertThat(result).containsExactly(userDto);
        verify(repository).findAll();
        verify(mapper).toUserDto(user);
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(repository).findById(1L);
        verify(mapper).toUserDto(user);
    }

    @Test
    void getUserByIdOrThrow_ShouldThrow_WhenUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByIdOrThrow(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    @Test
    void getUserByIdOrValidation_ShouldReturnUserDto_WhenUserExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserByIdOrValidation(1L);

        assertThat(result).isEqualTo(userDto);
        verify(repository).findById(1L);
        verify(mapper).toUserDto(user);
    }

    @Test
    void getUserByIdOrValidation_ShouldThrowValidationException_WhenUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByIdOrValidation(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    @Test
    void create_ShouldReturnUserDto_WhenSuccess() {
        when(mapper.toUser(userDto)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.create(userDto);

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void create_ShouldThrowDataAlreadyExistsException_WhenEmailExists() {
        when(mapper.toUser(userDto)).thenReturn(user);
        when(repository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(DataAlreadyExistsException.class)
                .hasMessageContaining("Пользователь с email=" + userDto.getEmail() + " уже существует");
    }

    @Test
    void update_ShouldUpdateNameAndEmail_WhenValid_NewEmailDifferent() {
        UserDto updatedDto = new UserDto(null, "Jane Doe", "jane@example.com");
        User updatedUser = new User(1L, "Jane Doe", "jane@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        // Для проверки email, в репозитории возвращаем List с юзером, у которого id совпадает с обновляемым
        when(repository.findByEmail(updatedDto.getEmail())).thenReturn(List.of(new User(1L, "Jane Doe", "jane@example.com")));
        when(repository.save(any(User.class))).thenReturn(updatedUser);
        when(mapper.toUserDto(updatedUser)).thenReturn(updatedDto);

        UserDto result = userService.update(updatedDto, 1L);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void update_ShouldUpdateName_WhenEmailNull() {
        UserDto updatedDto = new UserDto(null, "Jane Doe", null);
        User updatedUser = new User(1L, "Jane Doe", "john@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(updatedUser);
        when(mapper.toUserDto(updatedUser)).thenReturn(updatedDto);

        UserDto result = userService.update(updatedDto, 1L);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        // Email не меняется
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void update_ShouldThrowDataAlreadyExistsException_WhenEmailExistsAndBelongsToOtherUser() {
        UserDto updatedDto = new UserDto(null, "Jane Doe", "existing@example.com");
        User existingUser = new User(2L, "Other", "existing@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail(updatedDto.getEmail())).thenReturn(List.of(existingUser));

        assertThatThrownBy(() -> userService.update(updatedDto, 1L))
                .isInstanceOf(DataAlreadyExistsException.class)
                .hasMessageContaining("Пользователь с email=" + updatedDto.getEmail() + " уже существует");
    }

    @Test
    void update_ShouldThrowDataNotFoundException_WhenUserNotFound() {
        UserDto updatedDto = new UserDto(null, "Jane Doe", "jane@example.com");

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(updatedDto, 1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    @Test
    void delete_ShouldCallDeleteById_WhenUserExists() {
        doNothing().when(repository).deleteById(1L);

        userService.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowDataNotFoundException_WhenUserNotExists() {
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(1L);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertThat(result).isEqualTo(user);
        verify(repository).findById(1L);
    }

    @Test
    void findUserById_ShouldThrowDataNotFoundException_WhenUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }
}
