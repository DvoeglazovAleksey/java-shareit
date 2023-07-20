package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.info("Поступил запрос @Get на эндпоинт: '/users/{id}' для получения user по id = {}", id);
        return service.findUserById(id);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил запрос @Post на эндпоинт: '/users' для создания user");
        return service.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable long id) {
        log.info("Поступил запрос @Patch на эндпоинт: '/users/{id}' для обновления user c id = {}", id);
        return service.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Поступил запрос @Delete на эндпоинт: '/users/{id}' для удаления user c id = {}", id);
        service.delete(id);
    }
}
