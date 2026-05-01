package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.PatientEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.enums.ErrorMessage;
import com.ilham.doctorbookingsystem.exception.CustomException;
import com.ilham.doctorbookingsystem.exception.ResourceNotFoundException;
import com.ilham.doctorbookingsystem.mapper.AppointmentMapper;
import com.ilham.doctorbookingsystem.model.request.BookAppointmentRequestDto;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import com.ilham.doctorbookingsystem.repository.AppointmentRepository;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import com.ilham.doctorbookingsystem.repository.PatientRepository;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import com.ilham.doctorbookingsystem.service.auth.JwtService;
import com.ilham.doctorbookingsystem.service.mail.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    private final EmailService emailService;

    @Transactional
    public AppointmentResponseDto bookAppointment(BookAppointmentRequestDto request, HttpServletRequest httpRequest){
        log.info("actionLog.bookAppointment.start");

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(30);

        if (request.getAppointmentDate().isBefore(today) || request.getAppointmentDate().isAfter(maxDate)) {
            throw new CustomException(ErrorMessage.APPOINTMENT_DATE_EXCEPTION);
        }

        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND));

        DoctorEntity doctorEntity = doctorRepository.findById(request.getDoctorId()).
                orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND));

        if (doctorEntity.getStatus() != ApprovalStatus.APPROVED){
            throw new CustomException(ErrorMessage.DOCTOR_NOT_APPROVED);
        }

        boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );
        if (isBooked){
            throw new CustomException(ErrorMessage.SLOT_ALREADY_BOOKED);
        }

        AppointmentEntity appointmentEntity = AppointmentEntity.builder()
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .patient(patientEntity)
                .doctor(doctorEntity)
                .build();

        appointmentRepository.save(appointmentEntity);

        emailService.sendAppointmentCreatedEmail(
                userEntity.getEmail(),
                doctorEntity.getUser().getFirstName() + " " + doctorEntity.getUser().getLastName(),
                appointmentEntity.getAppointmentDate(),
                appointmentEntity.getAppointmentTime()
        );

        log.info("actionLog.bookAppointment.end");
        return appointmentMapper.entityToDto(appointmentEntity);
    }


    public Page<AppointmentResponseDto> getMyAppointments(HttpServletRequest httpRequest, Pageable pageable){
        log.info("actionLog.getMyAppointments.start");

        UserEntity user = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        PatientEntity patientEntity = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND));

        Page<AppointmentEntity> appointmentEntities = appointmentRepository.findByPatientId(patientEntity.getId(), pageable);

        log.info("actionLog.getMyAppointments.end");
        return appointmentEntities.map(appointmentMapper::entityToDto);
    }


    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id, HttpServletRequest httpRequest){
        log.info("actionLog.cancelAppointment.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(httpRequest))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PATIENT_NOT_FOUND));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND));

        if (!Objects.equals(appointmentEntity.getPatient().getId(), patientEntity.getId())){
            throw new CustomException(ErrorMessage.CANNOT_CANCEL_APPOINTMENT);
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.CANCELLED || appointmentEntity.getStatus() == AppointmentStatus.COMPLETED){
            throw new CustomException(ErrorMessage.SLOT_ALREADY_CANCELED);
        }

        appointmentEntity.setStatus(AppointmentStatus.CANCELLED);
        appointmentEntity.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointmentEntity);

        log.info("actionLog.cancelAppointment.end");
        return appointmentMapper.entityToDto(appointmentEntity);
    }
}
