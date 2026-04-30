package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    Page<ReviewEntity> findByDoctorId(Long doctorId, Pageable pageable);


    @Query("SELECT AVG(r.rating) from ReviewEntity r WHERE r.doctor.id = :doctorId")
    Double findAverageRatingByDoctorId(Long doctorId);

    long countByDoctorId(Long doctorId);
}
