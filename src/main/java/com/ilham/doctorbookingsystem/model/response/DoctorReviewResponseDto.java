package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorReviewResponseDto {

    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String patientName;
}
