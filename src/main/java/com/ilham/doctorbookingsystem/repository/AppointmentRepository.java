package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    Page<AppointmentEntity> findByDoctorId(Long id, Pageable pageable);

    Page<AppointmentEntity> findByPatientId(Long id, Pageable pageable);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, LocalTime time);

}
