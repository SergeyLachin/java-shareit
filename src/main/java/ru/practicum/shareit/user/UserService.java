package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class  UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserDto dto) {
        validateUser(dto);
        User user = UserMapper.toUser(dto);
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь {}.", savedUser);
        return UserMapper.doUserDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto findUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден."));
        log.info("Найден пользователь с айди {}.", id);
        return UserMapper.doUserDto(user);
    }

    public UserDto updateUser(UserDto dto, long id) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден."));
        if (dto.getId() == null) {
            dto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с ID=" + id + " не найден!"));
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if ((dto.getEmail() != null) && (dto.getEmail() != user.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(dto.getEmail()))
                    .allMatch(u -> u.getId().equals(dto.getId()))) {
                user.setEmail(dto.getEmail());
            } else {
                throw new ObjectNotFoundException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }

        User savedUser = userRepository.save(oldUser);
        log.info("Пользователь с айди {} успешно обновлен.", id);
        return UserMapper.doUserDto(savedUser);
    }

    public void removeUserById(long id) {
        userRepository.deleteById(id);
        log.info("Пользователь с айди успешно удален {}.", id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::doUserDto)
                .collect(Collectors.toList());
        log.info("Всё пользователи успешно получены.");
        return users;
    }

    public void validateUser(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            log.warn("Ошибка в данных - неверный адрес электронной почты {}", userDto.getEmail());
            throw new ObjectNotFoundException("Неверный адрес электронной почты");
        }
        List<UserDto> users = findAll();
        for (UserDto user1 : users) {
            if (userDto.getEmail().contains(user1.getEmail())) {
                throw new ObjectNotFoundException("Неверный адрес электронной почты");
            }
        }
    }

    public static void checkUserAvailability(UserRepository dao, long id) {
        if (!dao.existsById(id)) {
            throw new ObjectNotFoundException("Пользователь с запрашиваемым айди не зарегистрирован.");
        }
    }
}