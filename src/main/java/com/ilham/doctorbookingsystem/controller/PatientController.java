package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.BookAppointmentRequestDto;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import com.ilham.doctorbookingsystem.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;

    @PostMapping("/book-appointment")
    public AppointmentResponseDto bookAppointment(@RequestBody BookAppointmentRequestDto request, HttpServletRequest httpRequest){
        return appointmentService.bookAppointment(request,httpRequest);
    }

    @GetMapping("/get-all-my-appointments")
    public Page<AppointmentResponseDto> getAllAppointments(HttpServletRequest httpRequest, Pageable pageable){
        return appointmentService.getMyAppointments(httpRequest, pageable);
    }

    @PutMapping("/cancel-appointment/{id}")
    public AppointmentResponseDto cancelAppointment(@PathVariable Long id, HttpServletRequest httpRequest){
        return appointmentService.cancelAppointment(id, httpRequest);
    }

}
