package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
