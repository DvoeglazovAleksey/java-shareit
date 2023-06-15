package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        userRepository.findUserById(userId);
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getItemsByText(text).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validItemAndUserInAddItem(userId, itemDto);
        return ItemMapper.toItemDto(itemRepository.addItem(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validItemAndUserInUpdate(userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    private void validItemAndUserInAddItem(long userId, ItemDto itemDto) {
        userRepository.findUserById(userId);
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Name in addItem");
        } else if (itemDto.getDescription() == null) {
            throw new ValidationException("Description in addItem");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available in addItem");
        }
    }

    private Item validItemAndUserInUpdate(long userId, long itemId, ItemDto itemDto) {
        userRepository.findUserById(userId);
        Item item = itemRepository.getItemById(itemId);
        User owner = item.getOwner();
        if (owner.getId() != userId) {
            throw new NotFoundException("У пользователя нет прав на изменения вещи");
        }
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
        return item;
    }
}
