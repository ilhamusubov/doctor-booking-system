package com.ilham.doctorbookingsystem.model.response;

import lombok.Data;

@Data
public class AverageReviewResponseDto {
    private long totalReviews;
    private double averageRating;
}
