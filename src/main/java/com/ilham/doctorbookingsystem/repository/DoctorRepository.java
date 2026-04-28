package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    Page<DoctorEntity> findAllByStatus(ApprovalStatus status, Pageable pageable);

    Optional<DoctorEntity> findByUserId(Long id);

}
