package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.mapper.DoctorMapper;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    public Page<DoctorResponseDto> getAllApprovedDoctors(Pageable pageable) {
        log.info("actionLog.getAllApprovedDoctors.start");

        Page<DoctorEntity> doctorEntities = doctorRepository.findAllByStatus(ApprovalStatus.APPROVED, pageable);

        Page<DoctorResponseDto> doctorResponseDtos = doctorEntities.map(doctorMapper::entityToResponse);

        log.info("actionLog.getAllApprovedDoctors.end");
        return doctorResponseDtos;
    }
}
