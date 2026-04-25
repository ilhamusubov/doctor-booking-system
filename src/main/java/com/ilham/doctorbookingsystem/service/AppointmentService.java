package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.PatientEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.mapper.AppointmentMapper;
import com.ilham.doctorbookingsystem.model.request.BookAppointmentRequestDto;
import com.ilham.doctorbookingsystem.model.request.LoginRequest;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import com.ilham.doctorbookingsystem.repository.AppointmentRepository;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import com.ilham.doctorbookingsystem.repository.PatientRepository;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import com.ilham.doctorbookingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponseDto bookAppointment(BookAppointmentRequestDto request, HttpServletRequest httpRequest){
        log.info("actionLog.bookAppointment.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        DoctorEntity doctorEntity = doctorRepository.findById(request.getDoctorId()).
                orElseThrow(() -> new RuntimeException("Doctor Not Found"));

        if (doctorEntity.getStatus() != ApprovalStatus.APPROVED){
            throw new RuntimeException("Doctor Not Approved");
        }

        boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );
        if (isBooked){
            throw new RuntimeException("This slot is already booked");
        }

        AppointmentEntity appointmentEntity = AppointmentEntity.builder()
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .patient(patientEntity)
                .doctor(doctorEntity)
                .build();

        appointmentRepository.save(appointmentEntity);

        log.info("actionLog.bookAppointment.end");
        return appointmentMapper.entityToDto(appointmentEntity);
    }
}
