package com.oa.poll.exceptions;

public class DbUpdateException extends RuntimeException {
    public DbUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
