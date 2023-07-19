package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;
import ru.practicum.shareit.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    Valid valid;

    @InjectMocks
    private UserServiceImpl userService;

    private final User expectedUser = new User(1L, "Jon", "jon.doe@mail.com");

    @Test
    void findAll_thenReturnedUser() {
        when(userRepository.findAll()).thenReturn(List.of(expectedUser));

        List<UserDto> actualUsers = userService.findAll();

        verify(userRepository).findAll();
        assertNotNull(actualUsers);
        assertEquals(expectedUser.getId(), actualUsers.get(0).getId());
        assertEquals(expectedUser.getName(), actualUsers.get(0).getName());
        assertEquals(expectedUser.getEmail(), actualUsers.get(0).getEmail());
    }

    @Test
    void findAll_thenEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> actualUsers = userService.findAll();

        assertNotNull(actualUsers);
        assertEquals(0, actualUsers.size());
    }

    @Test
    void findUserById_thenReturnedUserDto() {
        long userId = expectedUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUserDto = userService.findUserById(userId);

        assertNotNull(actualUserDto);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserById_thenReturnedNotFound() {
        long userId = expectedUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.findUserById(userId));
    }

    @Test
    void addUser_thenAddUser() {
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUserDto = userService.addUser(UserMapper.toUserDto(expectedUser));

        verify(userRepository).save(expectedUser);
        assertEquals(actualUserDto.getName(), expectedUser.getName());
        assertEquals(actualUserDto.getEmail(), expectedUser.getEmail());
    }

    @Test
    void addUser_thenNotValidEmail() {
        UserDto notValidUserDto = new UserDto(0, "User", null);

        assertThrows(ValidationException.class,
                () -> userService.addUser(notValidUserDto));

        verify(userRepository, never()).save(UserMapper.toUser(notValidUserDto));
    }

    @Test
    void addUser_thenNotValidName() {
        UserDto notValidUserDto = new UserDto(0, null, "u@mail");

        assertThrows(ValidationException.class,
                () -> userService.addUser(notValidUserDto));

        verify(userRepository, never()).save(UserMapper.toUser(notValidUserDto));
    }

    @Test
    void updateUser_thenUpdateUser() {
        long userId = 1L;
        User oldUser = new User(1L, "User", "e@email.ru");
        User updateUser = new User(1L, "newUser", "new@email.ru");
        UserDto newUserDto = new UserDto(1L, "newUser", "new@email.ru");
        when(valid.checkUser(userId)).thenReturn(oldUser);
        when(userRepository.save(updateUser)).thenReturn(updateUser);

        UserDto actualUserDto = userService.updateUser(userId, newUserDto);

        verify(userRepository).save(updateUser);
        assertEquals("newUser", actualUserDto.getName());
        assertEquals("new@email.ru", actualUserDto.getEmail());
    }

    @Test
    void updateUser_thenNotValidNameUserDto() {
        long userId = 1L;
        User oldUser = new User(1L, "User", "e@email.ru");
        User notValidUser = new User(1L, "", "new@email.ru");
        UserDto notValidUserDto = new UserDto(1L, "", "new@email.ru");
        when(valid.checkUser(userId)).thenReturn(oldUser);

        assertThrows(ValidationException.class,
                () -> userService.updateUser(userId, notValidUserDto));

        verify(userRepository, never()).save(notValidUser);
    }

    @Test
    void updateUser_thenNotValidEmailUserDto() {
        long userId = 1L;
        User oldUser = new User(1L, "User", "e@email.ru");
        User notValidUser = new User(1L, "", "new@email.ru");
        UserDto notValidUserDto = new UserDto(1L, "UserDto", "");
        when(valid.checkUser(userId)).thenReturn(oldUser);

        assertThrows(EmailException.class,
                () -> userService.updateUser(userId, notValidUserDto));

        verify(userRepository, never()).save(notValidUser);
    }

    @Test
    void delete() {
        long userId = expectedUser.getId();
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }
}