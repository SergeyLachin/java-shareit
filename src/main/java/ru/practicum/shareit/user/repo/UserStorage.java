package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
    User createUser(User user) throws ConflictException;

    User updateUser(User user, Long id) throws ConflictException;

    Collection<User> getAllUsers();

    User getUser(Long id);

    User deleteUser(Long id);
}
