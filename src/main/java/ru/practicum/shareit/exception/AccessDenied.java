package ru.practicum.shareit.exception;

import java.nio.file.AccessDeniedException;

public class AccessDenied extends AccessDeniedException {

    public AccessDenied(String message) {
        super(message);
    }

}
