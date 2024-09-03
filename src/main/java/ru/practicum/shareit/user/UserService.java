package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    Long id = 0L;
    private User user;

    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        user = UserMapper.tuUser(userDto);
        return UserMapper.tuUserDto(userStorage.createUser(user));
    }

    public List<UserDto> getUsers() {
        List<User> users = userStorage.getUsers();
        List<UserDto> dtoUsers = new ArrayList<>();
        for (User user1 : users) {
            dtoUsers.add(UserMapper.tuUserDto(user1));
        }
        return dtoUsers;
    }

    public UserDto  updateUser(Long id, UserDto userDto) {
        if (getUserById(id) == null) {
            throw new ObjectNotFoundException("Пользователь с запрашиваемым id не зарегистрирован.");
        }
        UserDto oldUser = getUserById(id);
        if (!Objects.equals(userDto.getEmail(), oldUser.getEmail())) {
            checkUserUniqueness(userDto);
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            oldUser.setEmail(userDto.getEmail());
        }
        user = UserMapper.tuUser(oldUser);
        return UserMapper.tuUserDto(userStorage.updateUser(id, user));
    }

    public UserDto getUserById(Long id) {
        return UserMapper.tuUserDto(userStorage.getUserById(id)) ;
    }

    public void deleteUserById(Long id) {
        userStorage.getUserById(id);
    }

    public void validateUser(UserDto userDto) {
        if (userDto.getId() == null || userDto.getId() <= 0) {
            userDto.setId(++id);
            log.info("Некорректно указан id.");
        }
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            log.warn("Ошибка в данных - неверный адрес электронной почты {}", userDto.getEmail());
            throw new ObjectNotFoundException("Неверный адрес электронной почты");
        }
        List<UserDto> users = getUsers();
        for (UserDto user1 : users) {
            if (userDto.getEmail().contains(user1.getEmail())) {
                throw new ObjectNotFoundException("Неверный адрес электронной почты");
            }
        }
    }

    private void checkUserUniqueness(UserDto user) {
        String email = user.getEmail();
        boolean match = getUsers().stream().map(UserDto::getEmail).anyMatch(mail -> Objects.equals(mail, email));
        if (match) {
            throw new AlreadyExistException(user);
        }
    }
}