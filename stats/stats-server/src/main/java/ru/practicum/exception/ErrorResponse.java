package ru.practicum.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;

    ErrorResponse(String error) {
        this.error = error;
    }

}
