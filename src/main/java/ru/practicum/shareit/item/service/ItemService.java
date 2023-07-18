package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByOwnerId(long userId, int from, int size);

    List<ItemDto> getItemsByText(String text, int from, int size);

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userIs, long itemId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
