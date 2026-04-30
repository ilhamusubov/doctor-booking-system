package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.*;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.mapper.ReviewMapper;
import com.ilham.doctorbookingsystem.model.request.CreateReviewRequestDto;
import com.ilham.doctorbookingsystem.model.response.AverageReviewResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorReviewResponseDto;
import com.ilham.doctorbookingsystem.model.response.ReviewResponseDto;
import com.ilham.doctorbookingsystem.repository.*;
import com.ilham.doctorbookingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;
    private final DoctorRepository doctorRepository;

    public ReviewResponseDto createReview(CreateReviewRequestDto request, HttpServletRequest httpRequest){
        log.info("ActionLog.createReview.start");

        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment Not Found"));

        DoctorEntity doctorEntity = appointmentEntity.getDoctor();

        if (!Objects.equals(appointmentEntity.getPatient().getId(), patientEntity.getId())){
            throw new RuntimeException("You cannot review this appointment");
        }
        if (appointmentEntity.getStatus() != AppointmentStatus.COMPLETED){
            throw new RuntimeException("Only completed appointments can be reviewed");
        }
        if (reviewRepository.existsByAppointmentId(request.getAppointmentId())){
            throw new RuntimeException("Review already exists for this appointment");
        }

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .patient(patientEntity)
                .doctor(doctorEntity)
                .appointment(appointmentEntity)
                .build();

        reviewRepository.save(reviewEntity);

        log.info("ActionLog.createReview.end");
        return reviewMapper.entityToDto(reviewEntity);
    }


    public Page<DoctorReviewResponseDto> getMyReviews(Pageable pageable, HttpServletRequest httpRequest){
        log.info("ActionLog.getAllReviews.start");

        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Doctor Not Found"));

        Page<ReviewEntity> reviewEntities = reviewRepository.findByDoctorId(doctorEntity.getId(), pageable);

        log.info("ActionLog.getAllReviews.end");
        return reviewEntities.map(reviewMapper::entityToForDto);
    }


    public AverageReviewResponseDto getDoctorAverageRating(Long doctorId){
        log.info("ActionLog.averageReview.start");

        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor Not Found"));

        long totalReviews = reviewRepository.countByDoctorId(doctorId);
        Double averageRating = reviewRepository.findAverageRatingByDoctorId(doctorId);

        if (averageRating == null){
            averageRating = 0.0;
        }

        averageRating = Math.round(averageRating * 10.0) / 10.0;

        AverageReviewResponseDto response = new AverageReviewResponseDto();
        response.setTotalReviews(totalReviews);
        response.setAverageRating(averageRating);

        log.info("ActionLog.averageReview.end");
        return response;
    }

}
