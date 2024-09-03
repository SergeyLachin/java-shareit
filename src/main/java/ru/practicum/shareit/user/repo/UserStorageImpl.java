package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    public User updateUser(Long id, User user) {
        User oldUser = users.get(id);
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь с запрашиваемым id не зарегистрирован.");
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        users.remove(id);
        users.put(id, oldUser);
        log.info("Пользователь с айди успешно обновлен {}", id);
        return oldUser;
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
}