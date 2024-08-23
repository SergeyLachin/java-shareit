package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    List<User> getUsers();

    void updateUser(Integer id, User user);

    void deleteUsers();

    User getUserById(Integer id);
}
