package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceIT {

    private final EntityManager em;
    private final UserService service;
    private final UserRepository userRepository;

    @Test
    void saveUser() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        service.addUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser_thenNotValidEmailUser() {

        UserDto userDto = makeUserDto("UserDto", "some@email.com");
        User user = new User(null, "User", "e@email.com");
        User user2 = new User(null, "User2", "some@email.com");
        String message = String.format("Пользователь с email %s уже существует", userDto.getEmail());
        em.persist(user);
        em.persist(user2);

        EmailException exception = assertThrows(
                EmailException.class,
                () -> service.updateUser(user.getId(), userDto)
        );

        assertEquals(message, exception.getMessage());
    }


    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
