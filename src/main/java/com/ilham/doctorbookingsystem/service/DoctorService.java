package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.mapper.DoctorMapper;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    private DoctorEntity getDoctorById(Long id) {
        return doctorRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @Transactional
    public DoctorResponseDto approveDoctor(Long id) {
        log.info("ActionLog.approveDoctor.start");
        DoctorEntity doctor = getDoctorById(id);

        if (doctor.getStatus() == ApprovalStatus.APPROVED) {
            throw new RuntimeException("Doctor is already approved");
        }

            doctor.setStatus(ApprovalStatus.APPROVED);

        doctorRepository.save(doctor);

        log.info("ActionLog.approveDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }

    public List<DoctorResponseDto> getAllPendingDoctors() {
        log.info("ActionLog.getPendingDoctors.start");
        List<DoctorEntity> doctorEntities = doctorRepository.findAllByStatus(ApprovalStatus.PENDING);
        List<DoctorResponseDto> doctorResponseDtos = new ArrayList<>();
        for (DoctorEntity doctorEntity : doctorEntities) {
                doctorResponseDtos.add(doctorMapper.entityToResponse(doctorEntity));
        }
        log.info("ActionLog.getPendingDoctors.end");
        return doctorResponseDtos;
    }

    @Transactional
    public DoctorResponseDto rejectDoctor(Long id) {
        log.info("ActionLog.rejectDoctor.start");
        DoctorEntity doctor = getDoctorById(id);
        if (doctor.getStatus() == ApprovalStatus.REJECTED) {
            throw new RuntimeException("Doctor is already rejected");
        }
        doctor.setStatus(ApprovalStatus.REJECTED);
        doctorRepository.save(doctor);
        log.info("ActionLog.rejectDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }
}
