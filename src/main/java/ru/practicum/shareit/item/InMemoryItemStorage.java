package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    public Map<Long, Item> items;
    private Long currentId;

    public InMemoryItemStorage() {
        currentId = 0L;
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        if (isItemValid(item)) {
            item.setId(++currentId);
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null) {
            throw new ValidationException("Получен пользователь с пустым id");
        }
        if (!items.containsKey(item.getId())) {
            throw new DataNotFoundException("Вещь с id " + item.getId() + " не найдена");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        if (isItemValid(item)) {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Получен пользователь с пустым id");
        }
        if (!items.containsKey(itemId)) {
            throw new DataNotFoundException("Вещь с id " + itemId + " не найдена");
        }
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(toList()));
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        List<Long> deleteIds = new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .map(item -> item.getOwnerId())
                .collect(toList()));
        for (Long deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new DataNotFoundException("Вещь с id " + itemId + " не найдена");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    private boolean isItemValid(Item item) {
        if ((item.getName().isEmpty()) || (item.getDescription().isEmpty()) || (item.getAvailable() == null)) {
            throw new ValidationException("Некорректное описание вещи");
        }
        return true;
    }
}