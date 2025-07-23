package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Добавлен пользователь");
        return userClient.create(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Обновлён пользователь с id={}", userId);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Удалён пользователь с id={}", userId);
        return userClient.delete(userId);
    }
}