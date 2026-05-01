package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

@Data
public class DoctorDetailResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private Double averageRating;
    private Long totalReviews;
}
