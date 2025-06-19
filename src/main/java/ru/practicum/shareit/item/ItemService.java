package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;

import java.util.List;

@Service
public class ItemService {
    private ItemStorage itemStorage;

    @Autowired
    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public Item create(Item item) {
        return itemStorage.create(item);
    }

    public List<Item> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId);
    }

    public Item getItemById(Long id) {
        return itemStorage.getItemById(id);
    }

    public Item update(Item item, Long id) {
        if (item.getId() == null) {
            item.setId(id);
        }
        Item oldItem = itemStorage.getItemById(item.getId());
        if (oldItem.getOwnerId() != item.getOwnerId()) {
            throw new DataNotFoundException("Предмет не найден");
        }
        return itemStorage.update(item);
    }

    public Item delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (item.getOwnerId() != ownerId) {
            throw new DataNotFoundException("Предмет не найден");
        }
        return itemStorage.delete(itemId);
    }

    public void deleteItemsByOwner(Long ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }

    public List<Item> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(text);
    }

}
