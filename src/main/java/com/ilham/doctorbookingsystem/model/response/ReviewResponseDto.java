package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private DoctorResponseDto doctor;
}