package com.SAMUDRA.messaging_system.Exception;

import org.springframework.http.HttpStatus;

public class MessageException extends RuntimeException {

    private final HttpStatus status;

    // Default constructor (BAD_REQUEST)
    public MessageException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    // Custom status constructor
    public MessageException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}