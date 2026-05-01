package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.CreateReviewRequestDto;
import com.ilham.doctorbookingsystem.model.response.AverageReviewResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorDetailResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorReviewResponseDto;
import com.ilham.doctorbookingsystem.model.response.ReviewResponseDto;
import com.ilham.doctorbookingsystem.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create-review")
    public ReviewResponseDto createReview(@RequestBody @Valid CreateReviewRequestDto requestDto, HttpServletRequest httpRequest){
        return reviewService.createReview(requestDto, httpRequest);
    }

    @GetMapping("/get-my-reviews")
    public Page<DoctorReviewResponseDto> getMyReviews(Pageable pageable, HttpServletRequest httpRequest){
        return reviewService.getMyReviews(pageable, httpRequest);
    }

    @GetMapping("/get-doctor-average-rating/{doctorId}")
    public AverageReviewResponseDto getDoctorAverageRating(@PathVariable Long doctorId){
        return reviewService.getDoctorAverageRating(doctorId);
    }

    @GetMapping("/get-doctor-detail/{doctorId}")
    public DoctorDetailResponseDto getDoctorDetail(@PathVariable Long doctorId){
        return reviewService.getDoctorDetail(doctorId);
    }
}
