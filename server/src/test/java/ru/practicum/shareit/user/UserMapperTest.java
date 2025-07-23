package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toUserDto_ShouldMapAllFields() {
        User user = new User(1L, "John Doe", "john@example.com");

        UserDto dto = mapper.toUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void toUser_ShouldMapAllFields() {
        UserDto dto = new UserDto(2L, "Jane Smith", "jane@example.com");

        User user = mapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(dto.getId());
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }
}
