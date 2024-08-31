package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {

    private Long id = 0L;
    private Map<Long, User> users = new HashMap<>();

    private Long createId() {
        return ++id;
    }

    @Override
    public User createUser(User user) throws ConflictException {
        Collection<User> listUser = users.values();
        if (listUser.stream()
                .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("адрес почты уже занят");
        }
        Long id = createId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

//    @Override
//    public User updateUser(User user, Long id) {
//        checkUserAvailability(id);
//        User oldUser = users.get(id);
//
//        if (!Objects.equals(user.getEmail(), oldUser.getEmail())) {
//            checkUserUniqueness(user);
//        }
//        if (user.getName() != null && !user.getName().isBlank()) {
//            oldUser.setName(user.getName());
//        }
//        if (user.getEmail() != null && !user.getEmail().isBlank()) {
//            oldUser.setEmail(user.getEmail());
//        }
//
//        users.remove(id);
//        users.put(id, oldUser);
//        log.info("Пользователь с айди успешно обновлен {}", id);
//        return oldUser;
//    }

    @Override
    public User updateUser(User user, Long id) throws ConflictException {
        checkUserAvailability(id);
        User oldUser = users.get(id);

        if (!Objects.equals(user.getEmail(), oldUser.getEmail())) {
            checkUserUniqueness(user);
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
//        if (users.containsKey(id)) {
//            checkUserUniqueness(user);
////            Collection<User> listUser = users.values();
////            if (listUser.stream()
////                    .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
////                throw new ConflictException("адрес почты занят");
////            }
//            User user1 = users.get(id);
//            if (user.getName() != null && !Objects.equals(user.getName(), "")) user1.setName(user.getName());
//
//            if (user.getEmail() != null && !Objects.equals(user.getEmail(), "") && user.getEmail().contains("@")) {
//                user1.setEmail(user.getEmail());
//            }
//            return user1;
//        }
//
//        throw new NoSuchElementException("пользователь с таким идентификатором не найден");
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NoSuchElementException("пользователь с таким идентификатором не найден");

    }

    @Override
    public User deleteUser(Long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            users.remove(id);
            return user;
        }
        throw new NoSuchElementException("пользователь с таким идентификатором не найден");
    }

    private void checkUserAvailability(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с запрашиваемым айди не зарегистрирован.");
        }
    }

    private void checkUserUniqueness(User user) {
        String email = user.getEmail();
        boolean match = users.values().stream().map(User::getEmail).anyMatch(mail -> Objects.equals(mail, email));
        if (match) {
            throw new AlreadyExistException(user);
        }
    }
}
