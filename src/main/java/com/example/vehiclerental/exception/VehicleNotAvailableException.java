package com.example.vehiclerental.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Automatically returns a 400 Bad Request status code when thrown
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VehicleNotAvailableException extends RuntimeException {
    public VehicleNotAvailableException(String message) {
        super(message);
    }
}
