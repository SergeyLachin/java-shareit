package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
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
    public void updateUser(Integer id, User user) {
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
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователя с id: " + id + " нет.");
        }
        return users.get(id);
    }
}