package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInItemForOwner;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl service;

    @Mock
    ItemRepository itemRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingService bookingService;
    @Mock
    Valid valid;
    private final User user = new User(null, "User", "u@mail.com");
    private final User owner = new User(2L, "Павел", "p@mail.com");
    private final Item item = new Item(1L, "Кусторез", "Бывалый", true, owner, new ItemRequest());

    private final long userId = 1L;
    private final long itemId = 1L;
    private final int from = 0;

    private final int size = 30;
    private final PageRequest page = PageRequest.of(from, size);


    @Test
    void getItemById_thenReturnItem() {
        when(valid.checkItem(itemId)).thenReturn(item);
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(Collections.emptyList());

        ItemDto actualItem = service.getItemById(userId, 1L);

        verify(commentRepository, times(1)).findAllByItem_Id(itemId);
        assertEquals(actualItem.getDescription(), item.getDescription());
    }

    @Test
    void getItemById_thenReturnItemForOwner() {
        when(valid.checkItem(itemId)).thenReturn(item);
        when(bookingService.getNextBooking(itemId)).thenReturn(new BookingInItemForOwner());
        when(bookingService.getLastBooking(itemId)).thenReturn(new BookingInItemForOwner());
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(Collections.emptyList());

        ItemDto actualItem = service.getItemById(owner.getId(), itemId);

        verify(commentRepository, times(1)).findAllByItem_Id(itemId);
        assertEquals(actualItem.getDescription(), item.getDescription());
    }

    @Test
    void getItemsByOwnerId_thenReturnItems() {
        when(valid.checkUser(owner.getId())).thenReturn(owner);
        when(itemRepository.findByOwnerId(owner.getId(), page)).thenReturn(List.of(item));
        when(bookingService.getLastBooking(itemId)).thenReturn(new BookingInItemForOwner());
        when(bookingService.getNextBooking(itemId)).thenReturn(new BookingInItemForOwner());
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(Collections.emptyList());

        List<ItemDto> actualList = service.getItemsByOwnerId(owner.getId(), from, size);

        verify(bookingService, times(1)).getLastBooking(itemId);
        assertEquals(actualList.size(), 1);

    }

    @Test
    void getItemsByText_thenReturnItems() {
        String text = "кусторез";
        when(itemRepository.getItemsByText(text, page)).thenReturn(List.of(item));

        List<ItemDto> actualList = service.getItemsByText(text, from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getDescription(), "Бывалый");
    }

    @Test
    void getItemsByText_thenReturnEmptyList() {
        String text = "";

        List<ItemDto> actualList = service.getItemsByText(text, from, size);

        assertEquals(actualList.size(), 0);
    }

    @Test
    void addItem_thenAddItem() {
        when(valid.checkUser(owner.getId())).thenReturn(owner);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItem = service.addItem(owner.getId(), ItemMapper.toItemDto(item));

        assertEquals(actualItem.getName(), item.getName());
    }

    @Test
    void addItem_thenNotValidItemName() {
        item.setName("");

        assertThrows(ValidationException.class, () -> service.addItem(owner.getId(), ItemMapper.toItemDto(item)));
    }

    @Test
    void addItem_thenNotValidItemDescription() {
        item.setDescription(null);

        assertThrows(ValidationException.class, () -> service.addItem(owner.getId(), ItemMapper.toItemDto(item)));
    }

    @Test
    void addItem_thenNotValidIteAvailable() {
        item.setAvailable(null);

        assertThrows(ValidationException.class, () -> service.addItem(owner.getId(), ItemMapper.toItemDto(item)));
    }

    @Test
    void addComment_thenAddComment() {
        Comment comment = new Comment(1L, "Понравился", user, item, LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(valid.checkUser(userId)).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        when(commentRepository.save(any())).thenReturn(comment);
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(anyLong(),
                anyLong(), any(), any())).thenReturn(new Booking());

        CommentDto actualComment = service.addComment(userId, itemId, commentDto);

        assertEquals(actualComment.getText(), "Понравился");
    }

    @Test
    void addComment_thenNotValidOwner() {
        Comment comment = new Comment(1L, "Понравился", user, item, LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(valid.checkUser(userId)).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(anyLong(),
                anyLong(), any(), any())).thenReturn(null);

        assertThrows(ValidationException.class, () -> service.addComment(userId, itemId, commentDto));
    }

    @Test
    void updateItem_thenUpdateItem() {
        when(valid.checkUser(anyLong())).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ItemDto actualItem = service.updateItem(2L, itemId, itemDto);

        verify(itemRepository, times(1)).save(item);
        assertEquals(actualItem.getDescription(), "Бывалый");
    }

    @Test
    void updateItem_thenNotValidName() {
        item.setName("");
        when(valid.checkUser(anyLong())).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThrows(ValidationException.class, () -> service.updateItem(2L, itemId, itemDto));
    }

    @Test
    void updateItem_thenNotValidDescription() {
        item.setDescription("");
        when(valid.checkUser(anyLong())).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThrows(ValidationException.class, () -> service.updateItem(2L, itemId, itemDto));
    }

    @Test
    void updateItem_thenNotValidOwner() {
        when(valid.checkUser(anyLong())).thenReturn(user);
        when(valid.checkItem(itemId)).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThrows(ValidationException.class, () -> service.updateItem(userId, itemId, itemDto));
    }
}