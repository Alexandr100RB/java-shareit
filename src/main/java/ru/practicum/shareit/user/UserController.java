package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private UserService userService;
    private UserMapper mapper;
    private CheckConsistencyService checker;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper,
                          CheckConsistencyService checkConsistencyService) {
        this.userService = userService;
        this.mapper = userMapper;
        this.checker = checkConsistencyService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @Positive Long userId) {
        return mapper.toUserDto(userService.getUserById(userId));
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = userService.create(mapper.toUser(userDto));
        return mapper.toUserDto(user);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable @Positive Long userId) {
        User user = userService.update(mapper.toUser(userDto), userId);
        return mapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable @Positive Long userId) {
        UserDto userDto = mapper.toUserDto(userService.delete(userId));
        checker.deleteItemsByUser(userId);
        return userDto;
    }
}
