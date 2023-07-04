package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsersDto();

    UserDto findUserById(long id);

    UserDto addUserDto(UserDto userDto);

    UserDto updateUserDto(long id, UserDto userDto);

    void deleteUser(long id);
}
