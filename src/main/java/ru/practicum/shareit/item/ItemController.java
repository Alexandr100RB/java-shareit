package ru.practicum.shareit.item;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.CheckConsistencyService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final String OWNER = "X-Sharer-User-Id";
    private ItemService itemService;
    private ItemMapper mapper;
    private CheckConsistencyService checker;


    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper,
                          CheckConsistencyService checkConsistencyService) {
        this.itemService = itemService;
        this.mapper = itemMapper;
        this.checker = checkConsistencyService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return mapper.toItemDto(itemService.getItemById(itemId));
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        Item item = null;
        if (checker.isUserExists(ownerId)) {
            item = itemService.create(mapper.toItem(itemDto, ownerId));
        }
        return mapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        return itemService.getItemsByOwner(ownerId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long ownerId) {
        Item item = null;
        if (checker.isUserExists(ownerId)) {
            item = itemService.update(mapper.toItem(itemDto, ownerId), itemId);
        }
        return mapper.toItemDto(item);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        return mapper.toItemDto(itemService.delete(itemId, ownerId));
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        return itemService.getItemsBySearchQuery(text).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }
}