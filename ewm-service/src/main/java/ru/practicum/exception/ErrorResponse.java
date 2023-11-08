package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ErrorResponse {

    private String status;
    private String message;
    private String reason;
    private LocalDateTime time;

}
