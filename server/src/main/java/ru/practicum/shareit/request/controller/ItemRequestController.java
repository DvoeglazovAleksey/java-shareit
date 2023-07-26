package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос @Get на эндпоинт: '/requests' для получения списка itemRequests от пользователя с id= {}", userId);
        return itemRequestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam int from,
                                       @RequestParam int size) {
        log.info("Поступил запрос @Get на эндпоинт: '/requests/all' для получения списка itemRequests от пользователя с id= {}", userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Поступил запрос @Get на эндпоинт: '/{requestsId}' для получения itemRequest от пользователя с id= {}", userId);
        return itemRequestService.getById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Поступил запрос @Post на эндпоинт: '/requests' для создания itemRequest от пользователя с id= {}", userId);
        return itemRequestService.add(userId, itemRequestDto);
    }
}
