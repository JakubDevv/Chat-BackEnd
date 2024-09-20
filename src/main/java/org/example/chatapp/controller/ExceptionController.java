package org.example.chatapp.controller;

import org.example.chatapp.exception.ErrorMessage;
import org.example.chatapp.exception.chat.ChatNotFoundException;
import org.example.chatapp.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({
            ChatNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorMessage> notFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(
                        HttpStatus.NOT_FOUND.toString(),
                        e.getMessage(),
                        LocalDateTime.now()));
    }
}
