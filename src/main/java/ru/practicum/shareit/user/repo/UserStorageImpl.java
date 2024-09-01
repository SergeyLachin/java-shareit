package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users;

    public UserStorageImpl() {
        users = new HashMap<>();
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        log.info("Создан ползователь с id: {}.",  user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Список пользователей получен");
        return new ArrayList<>(users.values());
    }

    @Override
    public void updateUser(Long id, User user) {
        User oldUser = users.get(id);

        if (!Objects.equals(user.getEmail(), oldUser.getEmail())) {
            checkUserUniqueness(user);
        }
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Информация о пльзователе {} обновлена.", user.getId());
        } else {
            throw new ObjectNotFoundException("Пользователя с id: " + user.getId() + " нет.");
        }
    }

    @Override
    public void deleteUsers() {
        users.clear();
        log.info("Список пользователей очищен.");
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователя с id: " + id + " нет.");
        }
        return users.get(id);
    }

    private void checkUserUniqueness(User user) {
        String email = user.getEmail();
        boolean match = users.values().stream().map(User::getEmail).anyMatch(mail -> Objects.equals(mail, email));
        if (match) {
            throw new AlreadyExistException(user);
        }
    }
}