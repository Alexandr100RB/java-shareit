package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    public Map<Long, User> users;
    private Long currentId;

    public InMemoryUserStorage() {
        currentId = 0L;
        users = new HashMap<>();
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (users.values().stream().noneMatch(u -> u.getEmail().equals(user.getEmail()))) {
            if (isUserValid(user)) {
                user.setId(++currentId);
                users.put(user.getId(), user);
            }
        } else {
            throw new DataAlreadyExistsException("Пользователь с таким email уже существует");
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Получен пользователь с пустым id");
        }
        if (!users.containsKey(user.getId())) {
            throw new DataNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            if (isUserValid(user)) {
                users.put(user.getId(), user);
            }
        } else {
            throw new DataAlreadyExistsException("Пользователь с таким email уже существует");
        }
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public User delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Получен пользователь с пустым id");
        }
        if (!users.containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }
        return users.remove(userId);
    }

    private boolean isUserValid(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail: " + user.getEmail());
        }
        if ((user.getName().isEmpty())) {
            throw new ValidationException("Некорректный логин: " + user.getName());
        }
        return true;
    }
}
