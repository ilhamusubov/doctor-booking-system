package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByDoctorId(Long doctorId);

    boolean existsByAppointmentId(Long appointmentId);
}
