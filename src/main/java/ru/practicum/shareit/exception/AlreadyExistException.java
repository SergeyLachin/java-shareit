package ru.practicum.shareit.exception;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class AlreadyExistException extends RuntimeException {
    private static final String message = "%s уже зарегистрирован.";

    public AlreadyExistException(UserDto userDto) {
        super(
                String.format(message, userDto)
        );
    }

    public AlreadyExistException(Item item) {
        super(
                String.format(message, item)
        );
    }
}
