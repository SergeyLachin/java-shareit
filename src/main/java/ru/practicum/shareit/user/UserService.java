package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserStorage;

import java.util.List;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    Integer id = 0;
    private User user;

    public void createUser(User user) {
        this.user = user;
        validateUser(user);
        userStorage.createUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void updateUser(Integer id, User user) {
        if (user.getId() == null || user.getId() <= 0) {
            user.setId(id);
            log.info("Некорректно указан id.");
        }
            userStorage.updateUser(id, user);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(Integer id) {
        userStorage.getUserById(id);
    }

    public void validateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            user.setId(++id);
            log.info("Некорректно указан id.");
        }
        if (user.getEmail().contains(" ") || !user.getEmail().contains("@")) {
            log.warn("Data error - invalid Email {}", user.getEmail());
            throw new ObjectNotFoundException("Invalid Email");
        }
        List<User> users = getUsers();
        for (User user1 : users) {
            if (user.getEmail().contains(user1.getEmail())) {
                throw new ObjectNotFoundException("Invalid Email");
            }
        }
    }

}