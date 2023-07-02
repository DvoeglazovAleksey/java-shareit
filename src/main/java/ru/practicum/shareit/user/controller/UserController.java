package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> findAllUsers() {
        return service.findAllUsersDto();
    }

    @GetMapping("/{id}")
    public UserDto findUserDtoById(@PathVariable long id) {
        return service.findUserById(id);
    }

    @PostMapping
    public UserDto addUserDto(@Valid @RequestBody UserDto userDto) {
        return service.addUserDto(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserDto(@Valid @RequestBody UserDto userDto, @PathVariable long id) {
        return service.updateUserDto(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        service.deleteUser(id);
    }
}
