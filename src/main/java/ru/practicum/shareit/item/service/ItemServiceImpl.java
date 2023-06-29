package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = %s не зарегестрирована", String.valueOf(itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        userRepository.findById(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findAllByNameOrDescriptionLikeIgnoreCaseText(text.toLowerCase());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validItemAndUserInAddItem(userId, itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = %s не зарегестрирован", String.valueOf(userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item item1 = itemRepository.save(item);
        //validItemAndUserInAddItem(userId, itemDto);
        return ItemMapper.toItemDto(item1);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validItemAndUserInUpdate(userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    private void validItemAndUserInAddItem(long userId, ItemDto itemDto) {
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("У вещи не может быть пустым название");
        } else if (itemDto.getDescription() == null) {
            throw new ValidationException("У вещи не может быть пустым описание");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("У вещи не установлен статус доступности");
        }
    }

    private Item validItemAndUserInUpdate(long userId, long itemId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = %c не зарегестрирован", String.valueOf(userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = %s не зарегестрирована", String.valueOf(itemId)));
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Name не может быть пустым");
            }
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Description не может быть пустым");
            }
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        User owner = item.getOwner();
        if (owner.getId() != userId) {
            throw new ValidationException("У пользователя нет прав на изменения вещи");
        }
        return item;
    }
}
