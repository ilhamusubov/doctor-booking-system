package com.ilham.doctorbookingsystem.repository;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
}
