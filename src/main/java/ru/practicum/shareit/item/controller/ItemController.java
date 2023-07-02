package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
