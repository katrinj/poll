package com.oa.poll.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseStatusException handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMessages = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessages.put(error.getField(), error.getDefaultMessage());
        });
        LOGGER.warn(errorMessages.toString(), ex);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessages.toString(), ex);
    }

    @ExceptionHandler(DbUpdateException.class)
    public ResponseStatusException handleDbUpdateException(DbUpdateException ex) {
        LOGGER.warn(ex.getMessage(), ex);
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "DB could not be updated: " + ex.getMessage());
    }

    @ExceptionHandler(DbQueryException.class)
    public ResponseStatusException handleDbQueryException(DbQueryException ex) {
        LOGGER.warn(ex.getMessage(), ex);
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not retrieve data: " + ex.getMessage());
    }

    @ExceptionHandler(DoubleEntryException.class)
    public ResponseStatusException handleDoubleEntryException(DoubleEntryException ex) {
        LOGGER.warn(ex.getMessage(), ex);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

}
