package com.ilham.doctorbookingsystem.model.response;

import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentForDoctorResponseDto {
    private Long id;
    private PatientResponseDto patient;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}
