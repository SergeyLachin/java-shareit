package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    List<User> getUsers();

    User updateUser(Long id, User user);

    void deleteUsers();

    User getUserById(Long id);
}
