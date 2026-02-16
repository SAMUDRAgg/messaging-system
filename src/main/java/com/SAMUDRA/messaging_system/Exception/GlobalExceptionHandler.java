package com.SAMUDRA.messaging_system.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    /* ---------------- USER EXCEPTION ---------------- */

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> handleUserException(
            UserException ex,
            WebRequest request
    ) {

        ErrorDetail errorDetail = new ErrorDetail(
                "USER_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    /* ---------------- CHAT EXCEPTION ---------------- */

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorDetail> handleChatException(
            ChatException ex,
            WebRequest request
    ) {

        ErrorDetail errorDetail = new ErrorDetail(
                "CHAT_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorDetail, ex.getStatus());
    }

    /* ---------------- MESSAGE EXCEPTION ---------------- */

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetail> handleMessageException(
            MessageException ex,
            WebRequest request
    ) {

        ErrorDetail errorDetail = new ErrorDetail(
                "MESSAGE_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorDetail, ex.getStatus());
    }

    /* ---------------- GENERIC EXCEPTION ---------------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> handleOtherExceptions(
            Exception ex,
            WebRequest request
    ) {

        ErrorDetail errorDetail = new ErrorDetail(
                "INTERNAL_SERVER_ERROR",
                "Something went wrong!",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}