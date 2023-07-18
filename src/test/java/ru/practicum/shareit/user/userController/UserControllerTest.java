package ru.practicum.shareit.user.userController;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(1L, "John", "john.doe@mail.com");


    @Test
    @SneakyThrows
    void findAll() {
        when(userService.findAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void findById() {
        long userId = 1L;

        mvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findUserById(userId);
    }

    @Test
    void addUser_thenAddUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    void update_thenUpdateUser() {
        long userId = 1L;
        when(userService.updateUser(userId, userDto)).thenReturn(userDto);

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService).updateUser(anyLong(), any());
    }
    @Test
    @SneakyThrows
    void update_thenBadRequest() {
        long userId = 1L;
        UserDto userToUpdate = new UserDto();
        userToUpdate.setEmail("notValid");

        mvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userId, userToUpdate);
    }

    @Test
    @SneakyThrows
    void delete_thenIsOk() {
        long userId = 1L;

        mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}
