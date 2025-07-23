package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUsers_ShouldReturnList() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        when(userService.getUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));

        verify(userService).getUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).getUserById(1L);
    }

    @Test
    void create_ShouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto createdDto = new UserDto(1L, "John", "john@example.com");

        when(userService.create(any(UserDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).create(any(UserDto.class));
    }

    @Test
    void update_ShouldReturnUpdatedUser() throws Exception {
        UserDto userDto = new UserDto(null, "John Updated", "john.updated@example.com");
        UserDto updatedDto = new UserDto(1L, "John Updated", "john.updated@example.com");

        when(userService.update(any(UserDto.class), eq(1L))).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        verify(userService).update(any(UserDto.class), eq(1L));
    }

    @Test
    void delete_ShouldCallDelete() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}
