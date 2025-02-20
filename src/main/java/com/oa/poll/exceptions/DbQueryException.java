package com.oa.poll.exceptions;

public class DbQueryException extends RuntimeException {
    public DbQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
