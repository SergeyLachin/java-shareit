package ru.practicum.shareit.exception;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<Response> handleException(HttpServerErrorException.InternalServerError e) {
        log.info("InternalServerError {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response> handleException(NoSuchElementException e) {
        log.info("NoSuchElementException! {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Response> handleException(SQLException e) {
        log.info("SQLException! {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleException(final BadRequestException e) {
        log.error("BAD_REQUEST", e);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleException(final AccessDenied e) {
        log.error("CONFLICT", e);
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}