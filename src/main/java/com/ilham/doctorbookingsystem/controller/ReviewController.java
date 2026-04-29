package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.CreateReviewRequestDto;
import com.ilham.doctorbookingsystem.model.response.ReviewResponseDto;
import com.ilham.doctorbookingsystem.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create-review")
    public ReviewResponseDto createReview(@RequestBody @Valid CreateReviewRequestDto requestDto, HttpServletRequest httpRequest){
        return reviewService.createReview(requestDto, httpRequest);
    }
}
