package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorService doctorService;

    @GetMapping("/all/pending/doctors")
    public List<DoctorResponseDto> getAllPendingDoctors(){
        return doctorService.getAllPendingDoctors();
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
