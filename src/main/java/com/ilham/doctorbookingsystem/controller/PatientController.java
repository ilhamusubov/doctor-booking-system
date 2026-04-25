package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.BookAppointmentRequestDto;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import com.ilham.doctorbookingsystem.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;

    @PostMapping("/book-appointment")
    public AppointmentResponseDto bookAppointment(@RequestBody BookAppointmentRequestDto request, HttpServletRequest httpRequest){
        return appointmentService.bookAppointment(request,httpRequest);
    }
}
