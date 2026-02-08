package com.SAMUDRA.messaging_system.Exception;

import org.springframework.http.HttpStatus;

public class ChatException extends RuntimeException {

    private final HttpStatus status;

    public ChatException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ChatException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}