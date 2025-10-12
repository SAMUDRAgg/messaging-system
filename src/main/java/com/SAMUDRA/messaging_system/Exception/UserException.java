package com.SAMUDRA.messaging_system.Exception;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    // Constructor with message and default BAD_REQUEST
    public UserException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    // Constructor with message and custom HTTP status
    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
