package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidTest {
    @InjectMocks
    private Valid valid;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    private final long userId = 1L;

    @Test
    void checkUser_thenReturnUser() {
        User user = new User(userId, "User", "u@email.com");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        User result = valid.checkUser(userId);

        assertEquals(result.getName(), user.getName());
    }

    @Test
    void checkUser_thenReturnNotFoundException() {
        when(userRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> valid.checkUser(userId));
    }

    @Test
    void checkItem_thenReturnItem() {
        long itemId = 1L;
        Item item = new Item(itemId, "Trimer", "last", true, new User(),null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = valid.checkItem(itemId);

        assertEquals(result.getDescription(), item.getDescription());
    }

    @Test
    void checkItem_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> valid.checkItem(1L));
    }
}