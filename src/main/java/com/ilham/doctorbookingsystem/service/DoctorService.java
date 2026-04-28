package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.PatientEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.mapper.AppointmentMapper;
import com.ilham.doctorbookingsystem.mapper.DoctorMapper;
import com.ilham.doctorbookingsystem.model.response.AppointmentForDoctorResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.repository.AppointmentRepository;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import com.ilham.doctorbookingsystem.repository.PatientRepository;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import com.ilham.doctorbookingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final PatientRepository patientRepository;

    private DoctorEntity getDoctorById(Long id) {
        return doctorRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @Transactional
    public DoctorResponseDto approveDoctor(Long id) {
        log.info("ActionLog.approveDoctor.start");
        DoctorEntity doctor = getDoctorById(id);

        if (doctor.getStatus() == ApprovalStatus.APPROVED) {
            throw new RuntimeException("Doctor is already approved");
        }

            doctor.setStatus(ApprovalStatus.APPROVED);

        doctorRepository.save(doctor);

        log.info("ActionLog.approveDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }

    public List<DoctorResponseDto> getAllPendingDoctors() {
        log.info("ActionLog.getPendingDoctors.start");
        List<DoctorEntity> doctorEntities = doctorRepository.findAllByStatus(ApprovalStatus.PENDING);
        List<DoctorResponseDto> doctorResponseDtos = new ArrayList<>();
        for (DoctorEntity doctorEntity : doctorEntities) {
                doctorResponseDtos.add(doctorMapper.entityToResponse(doctorEntity));
        }
        log.info("ActionLog.getPendingDoctors.end");
        return doctorResponseDtos;
    }

    @Transactional
    public DoctorResponseDto rejectDoctor(Long id) {
        log.info("ActionLog.rejectDoctor.start");
        DoctorEntity doctor = getDoctorById(id);
        if (doctor.getStatus() == ApprovalStatus.REJECTED) {
            throw new RuntimeException("Doctor is already rejected");
        }
        doctor.setStatus(ApprovalStatus.REJECTED);
        doctorRepository.save(doctor);
        log.info("ActionLog.rejectDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }

    public Page<AppointmentForDoctorResponseDto> getAllMyAppointments(HttpServletRequest request, Pageable pageable) {
        log.info("ActionLog.getAllMyAppointments.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new RuntimeException("User not found"));


        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Page<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorId(doctorEntity.getId(), pageable);

        log.info("ActionLog.getAllMyAppointments.end");
        return appointmentEntities.map(appointmentMapper::entityToForDto);
    }


    @Transactional
    public AppointmentForDoctorResponseDto confirmAppointmentByDoctor(Long id, HttpServletRequest request) {
        log.info("ActionLog.confirmAppointment.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointmentEntity.getDoctor().getId().equals(doctorEntity.getId())) {
            throw new RuntimeException("You cannot confirm this appointment");
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.PENDING){
            appointmentEntity.setStatus(AppointmentStatus.CONFIRMED);
            appointmentEntity.setUpdatedAt(LocalDateTime.now());
        }else{
            throw new RuntimeException("You cannot confirm this appointment");
        }
        appointmentRepository.save(appointmentEntity);
        log.info("ActionLog.confirmAppointment.end");
        return appointmentMapper.entityToForDto(appointmentEntity);
    }


    @Transactional
    public AppointmentForDoctorResponseDto cancelAppointmentByDoctor(Long id, HttpServletRequest request) {
        log.info("ActionLog.rejectAppointment.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointmentEntity.getDoctor().getId().equals(doctorEntity.getId())) {
            throw new RuntimeException("You cannot cancel this appointment");
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.PENDING || appointmentEntity.getStatus() == AppointmentStatus.CONFIRMED){
            appointmentEntity.setStatus(AppointmentStatus.CANCELLED);
            appointmentEntity.setUpdatedAt(LocalDateTime.now());
        }else{
            throw new RuntimeException("Only pending or confirmed appointments can be cancelled");
        }
        appointmentRepository.save(appointmentEntity);
        log.info("ActionLog.rejectAppointment.end");
        return appointmentMapper.entityToForDto(appointmentEntity);
    }
}
