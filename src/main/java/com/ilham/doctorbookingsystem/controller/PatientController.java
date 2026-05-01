package com.ilham.doctorbookingsystem.controller;

import com.ilham.doctorbookingsystem.model.request.BookAppointmentRequestDto;
import com.ilham.doctorbookingsystem.model.response.AppointmentResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.service.AppointmentService;
import com.ilham.doctorbookingsystem.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;

    private final PatientService patientService;

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

    @GetMapping("/get-all-approved-doctors")
    public Page<DoctorResponseDto> getAllApprovedDoctors(Pageable pageable){
        return patientService.getAllApprovedDoctors(pageable);
    }

    @GetMapping("/get-doctor-by-specialization")
    public Page<DoctorResponseDto> searchDoctorBySpecialization(@RequestParam String specialization, Pageable pageable){
        return patientService.searchDoctorBySpecialization(specialization, pageable);
    }

    @GetMapping("/get-available-slots/{doctorId}")
    public List<LocalTime> getAvailableSlots(@PathVariable Long doctorId, @RequestParam LocalDate date){
        return patientService.getAvailableSlots(doctorId, date);
    }
}
