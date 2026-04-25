package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    Optional<PatientEntity> findByUserId(Long id);
}
