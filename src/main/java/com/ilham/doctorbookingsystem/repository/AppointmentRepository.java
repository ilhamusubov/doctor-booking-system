package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    Page<AppointmentEntity> findByDoctorId(Long id, Pageable pageable);

    Optional<AppointmentEntity> findByDoctorId(Long id);

    Page<AppointmentEntity> findByPatientId(Long id, Pageable pageable);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, LocalTime time);

    List<AppointmentEntity> findByDoctorIdAndAppointmentDateAndStatusNot(
            Long doctorId,
            LocalDate date,
            AppointmentStatus status
    );

}
