package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item getItemById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getItemsByText(String text);

    Item addItem(long userId, Item item);

    Item updateItem(Item item);

    Item deleteItemById(long id);
}
