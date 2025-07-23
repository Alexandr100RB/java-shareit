package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void getUsers_ShouldReturnOk() throws Exception {
        when(userClient.getUsers()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_ShouldReturnOk() throws Exception {
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_ShouldReturnOk() throws Exception {
        String json = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"john.doe@example.com\"\n" +
                "}";

        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturnOk() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Jane Updated\",\n" +
                "  \"email\": \"jane.updated@example.com\"\n" +
                "}";


        when(userClient.update(any(UserDto.class), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturnOk() throws Exception {
        when(userClient.delete(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
