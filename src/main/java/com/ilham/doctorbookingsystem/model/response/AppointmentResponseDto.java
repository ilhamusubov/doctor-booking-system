package com.ilham.doctorbookingsystem.model.response;


import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentResponseDto {
    private Long id;
    private DoctorResponseDto doctor;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
