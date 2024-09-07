package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    List<User> getUsers();

    User updateUser(Long id, User user);

    User getUserById(Long id);

    boolean isExist(Long id);

    void deleteUserById(Long id);
}
