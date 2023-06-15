package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> listItemsIdByUser = new HashMap<>();

    private long nextId = 1;

    @Override
    public Item getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("Данной вещи не существует");
        }
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return listItemsIdByUser.get(userId).stream()
                .map(items::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        List<Item> list = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.isAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    @Override
    public Item addItem(long userId, Item item) {
        long id = nextId++;
        item.setId(id);
        User owner = User.builder().id(userId).build();
        item.setOwner(owner);
        items.put(item.getId(), item);
        listItemsIdByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(id);
        log.info("Добавлена новая вещь с id{}", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь с id{}", item.getId());
        return item;
    }

    @Override
    public Item deleteItemById(long id) {
        Item item = getItemById(id);
        User owner = item.getOwner();
        listItemsIdByUser.get(owner.getId()).remove(id);
        return items.remove(owner.getId());
    }
}
