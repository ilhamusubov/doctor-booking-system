package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponseDto {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}
