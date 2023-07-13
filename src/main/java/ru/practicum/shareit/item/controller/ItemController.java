package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Поступил запрос @Get на эндпоинт: '/items/{itemId}' для получения item по itemId = {}" +
                " от пользователя с id = {}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "30") @Min(1) int size) {
        log.info("Поступил запрос @Get на эндпоинт: '/items' для получения items от пользователя с id = {}", userId);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text,
                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                       @RequestParam(defaultValue = "30") @Min(1) int size) {
        log.info("Поступил запрос @Get на эндпоинт: '/search' для получения items по поиску text = {}", text);
        return itemService.getItemsByText(text, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос @Post на эндпоинт: '/items' для создания item от пользователя с id = {}", userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос @Patch на эндпоинт: '/items/{itemId}' для обновления item от пользователя с id = {}", userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Поступил запрос @Post на эндпоинт: '/{itemId}/comment' для создания comment от пользователя с id = {}", userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
