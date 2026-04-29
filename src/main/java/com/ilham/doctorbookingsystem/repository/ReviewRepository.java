package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    Page<ReviewEntity> findByDoctorId(Long doctorId, Pageable pageable);
}
