package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        log.debug("Создан ползователь с id: {}.",  user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.debug("Список пользователей получен");
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(Long id, User user) {
        User oldUser = users.get(id);
        if (user.getEmail() != null) oldUser.setEmail(user.getEmail());
        if (user.getName() != null && !Objects.equals(user.getName(), "")) oldUser.setName(user.getName());
        users.put(id, oldUser);
        log.debug("Пользователь с айди {} успешно обновлен ", id);
        return oldUser;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователя с id: " + id + " нет.");
        }
        return users.get(id);
    }

    @Override
    public boolean isExist(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователя с id: " + id + " нет.");
        }
        return users.containsKey(id);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователя с id: " + id + " нет.");
        }
        users.remove(id);
    }
}