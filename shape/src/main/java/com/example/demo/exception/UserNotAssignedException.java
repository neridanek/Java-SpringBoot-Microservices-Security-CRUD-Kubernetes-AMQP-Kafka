package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotAssignedException extends RuntimeException {
    public UserNotAssignedException(String message) {
        super(message);
    }
}
