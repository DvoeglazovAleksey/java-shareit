package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsByText(String text);

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userIs, long itemId, ItemDto itemDto);
}
