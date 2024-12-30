package com.assignment.kam_lead_management_system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KamCustomException extends RuntimeException{

    private final HttpStatus status;

    public KamCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public KamCustomException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

}
