package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final Valid valid;

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = valid.checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDtoForOwner(item);
        if (userId == (item.getOwner().getId())) {
            itemDto.setLastBooking(bookingService.getLastBooking(itemId));
            itemDto.setNextBooking(bookingService.getNextBooking(itemId));
        } else {
            itemDto = ItemMapper.toItemDto(item);
        }
        itemDto.setComments(getCommentsByItemId(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long userId, int from, int size) {
        valid.checkUser(userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<ItemDto> itemDto = itemRepository.findByOwnerId(userId, page).stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemDtoForOwner).collect(Collectors.toList());
        for (ItemDto item : itemDto) {
            item.setLastBooking(bookingService.getLastBooking(item.getId()));
            item.setNextBooking(bookingService.getNextBooking(item.getId()));
            item.setComments(getCommentsByItemId(item.getId()));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByText(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from, size);
        List<Item> items = itemRepository.getItemsByText(text.toLowerCase(), page);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validItemAndUserInAddItem(itemDto);
        User owner = valid.checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item item1 = itemRepository.save(item);
        return ItemMapper.toItemDto(item1);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = valid.checkUser(userId);
        Item item = valid.checkItem(itemId);
        checkAuthor(userId, itemId);
        Comment comment = CommentMapper.toComment(commentDto, author, item);
        Comment comment2 = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment2);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validItemAndUserInUpdate(userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    private void validItemAndUserInAddItem(ItemDto itemDto) {
        if (itemDto.getName().isBlank()) {
            throw new ValidationException("У вещи не может быть пустым название");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("У вещи не может быть пустым описание");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("У вещи не установлен статус доступности");
        }
    }

    private Item validItemAndUserInUpdate(long userId, long itemId, ItemDto itemDto) {
        valid.checkUser(userId);
        Item item = valid.checkItem(itemId);
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

    private void checkAuthor(long userId, long itemId) {
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
        if (booking == null) {
            throw new ValidationException("Пользователь не имеет прав оставить комментарий");
        }
    }

    private List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
