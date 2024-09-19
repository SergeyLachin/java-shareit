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
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.repo.UserStorage;

import java.util.*;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserRepository userRepository;
    Long id = 0L;
    //private User user;


    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public UserDto  updateUser(Long id, UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с ID=" + id + " не найден!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new ObjectNotFoundException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }
        return UserMapper.toDto(userRepository.save(user));
    }


    public List<UserDto> findAllUsers() {
        Collection<User> list = userRepository.findAll();
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(UserMapper.toDto(user));
        }
        return listDto;
    }


    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + id + " не существует"));
        return UserMapper.toDto(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void validateUser(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            log.warn("Ошибка в данных - неверный адрес электронной почты {}", userDto.getEmail());
            throw new ObjectNotFoundException("Неверный адрес электронной почты");
        }
        List<UserDto> users = findAllUsers();
        for (UserDto user1 : users) {
            if (userDto.getEmail().contains(user1.getEmail())) {
                throw new ObjectNotFoundException("Неверный адрес электронной почты");
            }
        }
    }
}