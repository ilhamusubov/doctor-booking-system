package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorService doctorService;

    @GetMapping("/all/pending/doctors")
    public Page<DoctorResponseDto> getAllPendingDoctors(Pageable pageable){
        return doctorService.getAllPendingDoctors(pageable);
    }

    @PutMapping("/approve/doctor/{id}")
    public DoctorResponseDto approveDoctor(@PathVariable Long id){
        return doctorService.approveDoctor(id);
    }

    @PutMapping("/reject/doctor/{id}")
    public DoctorResponseDto rejectDoctor(@PathVariable Long id){
        return doctorService.rejectDoctor(id);
    }

}
