package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    List<DoctorEntity> findAllByStatus(ApprovalStatus status);

    Optional<DoctorEntity> findByUserId(Long id);

}
