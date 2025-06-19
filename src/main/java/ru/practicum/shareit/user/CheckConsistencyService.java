package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;

@Service
public class CheckConsistencyService {
    private UserService userService;
    private ItemService itemService;

    @Autowired
    public CheckConsistencyService(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public boolean isUserExists(Long userId) {
        boolean isExists = false;
        if (userService.getUserById(userId) != null) {
            isExists = true;
        }
        return isExists;
    }

    public void deleteItemsByUser(Long userId) {
        itemService.deleteItemsByOwner(userId);
    }
}
