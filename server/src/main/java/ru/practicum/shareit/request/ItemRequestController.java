package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.service = itemRequestService;
    }

    @ResponseBody
    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(USER_ID) @Positive Long requestorId) {
        log.info("Создан запрос вещи от пользователя с id={}", requestorId);
        return service.create(itemRequestDto, requestorId, LocalDateTime.now());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") @Positive Long itemRequestId,
                                             @RequestHeader(USER_ID) @Positive Long userId) {
        return service.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(USER_ID) @Positive Long userId) {
        return service.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID) @Positive Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return service.getAllItemRequests(userId, from, size);
    }
}