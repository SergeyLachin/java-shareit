package ru.practicum.shareit.exception;

import ru.practicum.shareit.item.model.Item;

public class AlreadyExistException extends RuntimeException {
    private static final String message = "%s уже зарегистрирован.";

    public AlreadyExistException(String userDto) {
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
