package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
}
