package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.response.AppointmentForDoctorResponseDto;
import com.ilham.doctorbookingsystem.service.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/get-all-my-appointments")
    public Page<AppointmentForDoctorResponseDto> getAllMyAppointments(HttpServletRequest request, Pageable pageable){
        return doctorService.getAllMyAppointments(request, pageable);
    }

    @PutMapping("/confirm-appointment")
    public AppointmentForDoctorResponseDto confirmAppointmentByDoctor(Long id, HttpServletRequest request){
        return doctorService.confirmAppointmentByDoctor(id, request);
    }

    @PutMapping("/cancel-appointment")
    public AppointmentForDoctorResponseDto cancelAppointmentByDoctor(Long id, HttpServletRequest request){
        return doctorService.cancelAppointmentByDoctor(id, request);
    }
}
