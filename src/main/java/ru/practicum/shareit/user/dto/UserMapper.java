package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {

    public static UserDto tuUserDto(User user)  {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}

