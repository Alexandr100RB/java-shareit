package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable @Positive Long itemId, @RequestHeader(OWNER) @Positive Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(OWNER) @Positive Long ownerId) {
        log.info("Добавлена вещи владельцем с id={}", ownerId);
        return itemService.create(itemDto, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) @Positive Long ownerId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(required = false) Integer size) {
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable @Positive Long itemId,
                          @RequestHeader(OWNER) @Positive Long ownerId) {
        log.info("Обновлена вещи с id={}", itemId);
        return itemService.update(itemDto, ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable @Positive Long itemId, @RequestHeader(OWNER) @Positive Long ownerId) {
        log.info("Удаленв вещи с id={}", itemId);
        itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(required = false) Integer size) {
        return itemService.getItemsBySearchQuery(text, from, size);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @RequestHeader(OWNER) Long userId,
                                    @PathVariable Long itemId) {
        log.info("Добавление отзыва пользователем с id={}", userId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}