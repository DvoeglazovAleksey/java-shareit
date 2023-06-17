package ru.practicum.shareit.user.repositoty;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAllUsers();

    User findUserById(long id);

    User addUser(User user);

    User updateUser(long id, User user);

    boolean deleteUser(long id);

    boolean isEmailPresentInRepository(User user);
}
