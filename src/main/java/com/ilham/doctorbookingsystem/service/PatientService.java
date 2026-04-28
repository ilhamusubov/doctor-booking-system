package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.mapper.DoctorMapper;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.repository.AppointmentRepository;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;
    private final AppointmentRepository appointmentRepository;

    public Page<DoctorResponseDto> getAllApprovedDoctors(Pageable pageable) {
        log.info("actionLog.getAllApprovedDoctors.start");

        Page<DoctorEntity> doctorEntities = doctorRepository.findAllByStatus(ApprovalStatus.APPROVED, pageable);

        Page<DoctorResponseDto> doctorResponseDtos = doctorEntities.map(doctorMapper::entityToResponse);

        log.info("actionLog.getAllApprovedDoctors.end");
        return doctorResponseDtos;
    }


    public Page<DoctorResponseDto> searchDoctorBySpecialization(String specialization, Pageable pageable) {
        log.info("ActionLog.searchApprovedDoctors.start");

        Page<DoctorEntity> doctorEntities;

        if (specialization == null || specialization.isBlank()) {
            doctorEntities = doctorRepository.findAllByStatus(ApprovalStatus.APPROVED, pageable);
        } else {
            doctorEntities = doctorRepository.findByStatusAndSpecializationContainingIgnoreCase(
                    ApprovalStatus.APPROVED,
                    specialization,
                    pageable
            );
        }

        Page<DoctorResponseDto> doctorResponseDtos =
                doctorEntities.map(doctorMapper::entityToResponse);

        log.info("ActionLog.searchApprovedDoctors.end");
        return doctorResponseDtos;
    }


    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {

        log.info("ActionLog.getAvailableSlots.start");

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(30);

        if (date.isBefore(today) || date.isAfter(maxDate)) {
            throw new RuntimeException("Date must be within next 30 days");
        }

        List<AppointmentEntity> appointmentEntities =
                appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(
                        doctorId,
                        date,
                        AppointmentStatus.CANCELLED
                );

        List<LocalTime> bookedTimes = new ArrayList<>();

        for (AppointmentEntity appointmentEntity : appointmentEntities) {
            bookedTimes.add(appointmentEntity.getAppointmentTime());
        }

        List<LocalTime> availableSlots = new ArrayList<>();

        for (int hour = 9; hour <= 17; hour++) {

            LocalTime slot = LocalTime.of(hour, 0);

            if (!bookedTimes.contains(slot)) {
                availableSlots.add(slot);
            }
        }

        log.info("ActionLog.getAvailableSlots.end");

        return availableSlots;
    }
}
