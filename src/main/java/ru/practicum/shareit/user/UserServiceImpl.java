package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataAlreadyExistsException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.mapper = userMapper;
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return getUserByIdOrThrow(id);
    }

    @Override
    public UserDto getUserByIdOrThrow(Long id) {
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден")));
    }

    @Override
    public UserDto getUserByIdOrValidation(Long id) {
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new ValidationException("Пользователь с id=" + id + " не найден")));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        try {
            return mapper.toUserDto(repository.save(mapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new DataAlreadyExistsException("Пользователь с email=" + userDto.getEmail() + " уже существует");
        }
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new DataAlreadyExistsException("Пользователь с email=" + user.getEmail() + " уже существует");
            }

        }
        return mapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    @Override
    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id=" + id + " не найден"));
    }
}